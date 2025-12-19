package social;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Post {

@GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
private Long postId;
private String content;
private Long timestamp = System.currentTimeMillis();


@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "authorCode")
private Person author;


// Aggiungi la relazione OnToMany e la generazione del codice univoco per il post

Post(){}

public Post(Person author, String content){
    this.author = author;
    this.content = content;
}

public Long getId(){
    return postId;
}

public String getContent(){
    return content;
}

public Long getTime(){
    return timestamp;
}
    
public Person getAuthor(){
    return author;
}

}
