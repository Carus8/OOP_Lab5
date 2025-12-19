package social;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Facade class for the social network system.
 * 
 */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  
  /**
   * Creates a new account for a person
   * 
   * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    if (personRepository.findById(code).isPresent()){    // check if db already contains the code
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);    // create the person as a POJO
    personRepository.save(person);                      // save it to db
  }

  /**
   * Retrieves information about the person given their account code.
   * The info consists in name and surname of the person, in order, separated by
   * blanks.
   * 
   * @param code account code
   * @return the information of the person
   * @throws NoSuchCodeException if a person with that code does not exist
   */
  public String getPerson(String code) throws NoSuchCodeException {
      Person person = personRepository.findById(code).orElseThrow(NoSuchCodeException::new);
      return person.getCode() + " " + person.getName() + " " + person.getSurname();
    }
    


  /**
   * Define a friendship relationship between two persons given their codes.
   * <p>
   * Friendship is bidirectional: if person A is adding as friend person B, that means
   * that person B automatically adds as friend person A.
   * 
   * @param codePerson1 first person code
   * @param codePerson2 second person code
   * @throws NoSuchCodeException in case either code does not exist
   */
  public void addFriendship(String codePerson1, String codePerson2) throws NoSuchCodeException{
    Person personA = personRepository.findById(codePerson1).orElseThrow(NoSuchCodeException::new);
    Person personB = personRepository.findById(codePerson2).orElseThrow(NoSuchCodeException::new);
    if(!personA.getFriends().contains(personB)){
      personA.getFriends().add(personB);
      personB.getFriends().add(personA);
      personRepository.update(personA);
      personRepository.update(personB);
    }
  }
      

  /**
   * Retrieve the collection of their friends given the code of a person.
   *
   * @param codePerson code of the person
   * @return the list of person codes
   * @throws NoSuchCodeException in case the code does not exist
   */
  public Collection<String> listOfFriends(String codePerson)
        throws NoSuchCodeException {
    Person person = personRepository.findById(codePerson)
            .orElseThrow(NoSuchCodeException::new);
    Collection<String> result = new ArrayList<>();
    for (Person friend : person.getFriends()) {
        result.add(friend.getCode());
    }
    return result;
}


  /**
   * Creates a new group with the given name
   * 
   * @param groupName name of the group
   * @throws GroupExistsException if a group with given name does not exist
   */
 private final GroupRepository groupRepository = new GroupRepository();

  public void addGroup(String groupName) throws GroupExistsException {
    if (groupRepository.findById(groupName).isPresent()) {
        throw new GroupExistsException();
    }
    Group g = new Group(groupName);
    groupRepository.save(g);
}


  /**
   * Deletes the group with the given name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if a group with given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    Group g = groupRepository.findById(groupName).orElseThrow(NoSuchCodeException::new);
    groupRepository.delete(g);
  }

  /**
   * Modifies the group name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if the original group name does not exist
   * @throws GroupExistsException if the target group name already exist
   */
  public void updateGroupName(String groupName, String newName)
        throws NoSuchCodeException, GroupExistsException {

    Group g = groupRepository.findById(groupName)
            .orElseThrow(NoSuchCodeException::new);

    if (groupRepository.findById(newName).isPresent()) {
        throw new GroupExistsException();
    }
    Set<Person> membersCopy = new HashSet<>(g.getMembers());
    groupRepository.delete(g);
    Group newGroup = new Group(newName);
    newGroup.getMembers().addAll(membersCopy);

    groupRepository.save(newGroup);
}


  /**
   * Retrieves the list of groups.
   * 
   * @return the collection of group names
   */

public Collection<String> listOfGroups() {
    Set<String> result = groupRepository.findAll().stream().map(s->s.getName()).collect(Collectors.toSet());
    return result;
}

  /**
   * Add a person to a group
   * 
   * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    Person newMember = personRepository.findById(codePerson).orElseThrow(NoSuchCodeException::new);
    Group group = groupRepository.findById(groupName).orElseThrow(NoSuchCodeException::new);
    group.addMember(newMember);
    groupRepository.update(group);
  }

  /**
   * Retrieves the list of people on a group
   * 
   * @param groupName name of the group
   * @return collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    return groupRepository.findById(groupName)
        .map(g -> g.getMembers().stream()
                   .map(Person::getCode)
                   .toList())
        .orElseGet(ArrayList::new);
}

  /**
   * Retrieves the code of the person having the largest
   * group of friends
   * 
   * @return the code of the person
   */
  public String personWithLargestNumberOfFriends() {
    return personRepository.findAll().stream()
        .max(Comparator.comparingInt(p -> p.getFriends().size()))
        .map(p -> p.getCode())
        .orElse(null);
}


  /**
   * Find the name of group with the largest number of members
   * 
   * @return the name of the group
   */
  public String largestGroup() {
    return groupRepository.findAll().stream()
    .max(Comparator.comparingInt(g->g.getMembers().size())).map(g->g.getName()).orElse(null);
  }

  /**
   * Find the code of the person that is member of
   * the largest number of groups
   * 
   * @return the code of the person
   */
  public String personInLargestNumberOfGroups() {

    return groupRepository.findAll().stream()                 
        .flatMap(g -> g.getMembers().stream())               
        .collect(Collectors.groupingBy(                      
            p -> p,
            Collectors.counting()
        ))
        .entrySet().stream()                                
        .max(Map.Entry.comparingByValue())                   
        .map(e -> e.getKey().getCode())                      
        .orElse(null);                                       
      }

  // R5

  /**
   * add a new post by a given account
   * 
   * @param authorCode the id of the post author
   * @param text   the content of the post
   * @return a unique id of the post
   */

  private final PostRepository postRepository = new PostRepository();


  public String post(String authorCode, String text){

    Person author = personRepository.findById(authorCode)
            .orElse(null);

    Post newPost = new Post(author, text);
    postRepository.save(newPost);

    return newPost.getId();
}



  /**
   * retrieves the content of the given post
   * 
   * @param pid    the id of the post
   * @return the content of the post
   */
  public String getPostContent(String pid) {
    Post p = postRepository.findById(pid).orElse(null);
    return p.getContent();
  }

  /**
   * retrieves the timestamp of the given post
   * 
   * @param pid    the id of the post
   * @return the timestamp of the post
   */
  public long getTimestamp(String pid) {
    Post p = postRepository.findById(pid).orElse(null);
    return p.getTime();
  }

  /**
   * returns the list of post of a given author paginated
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts id
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    Person p = personRepository.findById(author).orElse(null);
    List<String>posts = p.getPosts().stream().sorted(Comparator.comparingLong(Post::getTime).reversed()).map(Post::getId).toList();
    int start = pageLength*(pageNo-1);
    List<String>result = new ArrayList<>();
    for(int i=start; i<posts.size(); i++) result.add(posts.get(i));
    return result;
  }

  /**
   * returns the paginated list of post of friends.
   * The returned list contains the author and the id of a post separated by ":"
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts key elements
   */
  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {

    Person person = personRepository.findById(author).orElse(null);
    if (person == null) return new ArrayList<>();
    List<Post> posts = person.getFriends().stream()
        .flatMap(f -> f.getPosts().stream())
        .sorted(Comparator.comparingLong(Post::getTime).reversed())
        .toList();
    int start = pageLength * (pageNo - 1);
    List<String> result = new ArrayList<>();
    for (int i = start; i < start + pageLength && i < posts.size(); i++) {
        Post p = posts.get(i);
        result.add(p.getAuthor().getCode() + ":" + p.getId());
    }

    return result;
}


}