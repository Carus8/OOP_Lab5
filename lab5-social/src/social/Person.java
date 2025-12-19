package social;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
  name = "person_friend", 
  joinColumns = @JoinColumn(name = "person_id"),
  inverseJoinColumns = @JoinColumn(name = "friend_id"))

  private Set<Person> friends = new HashSet<>();

@OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
private List<Post> posts = new ArrayList<>();

  Person() {
    // default constructor is needed by JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {
    return code;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }

  Set<Person>getFriends(){
    return friends;
  }

  List<Post> getPosts(){
    return posts;
  }
}
