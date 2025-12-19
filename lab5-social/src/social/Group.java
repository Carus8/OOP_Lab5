package social;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table (name = "Groups")
public class Group {

    @Id
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_person",
        joinColumns = @JoinColumn(name = "group_name"),
        inverseJoinColumns = @JoinColumn(name = "person_code")
    )
    private Set<Person> members = new HashSet<>();

    Group() {}

    public Group(String name) {
        this.name = name;
    }

    public Set<Person> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void addMember(Person p) {
        members.add(p);
    }
}
