package pl.javastart.library.model;

import java.io.Serializable;
import java.util.*;

import pl.javastart.library.app.exception.PublicationAlreadyExistsException;
import pl.javastart.library.app.exception.UserAlreadyExistsException;

public class Library implements Serializable {

    private Map<String, Publication> publications = new HashMap<>();
    private Map<String, User> users = new HashMap<>();


    public Map<String, Publication> getPublications() {
        return publications;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Collection<Publication> getSortedPublications(Comparator<Publication> comparator){
        ArrayList<Publication> list = new ArrayList<>(this.publications.values());
        list.sort(comparator);
        return list;
    }

    public Collection<User> getSortedUsers(Comparator<User> comparator){
        ArrayList<User> list = new ArrayList<>(this.users.values());
        list.sort(comparator);
        return list;
    }

    public Optional<Publication> getPublicationByTitle(String title){
        return Optional.ofNullable(publications.get(title));
    }

    public void addUser(LibraryUser user){
        if(users.containsKey(user.getPesel())){
            throw new UserAlreadyExistsException(
                    "Użytkownik ze wskazanym peselem już istnieje " + user.getPesel()
            );
        }
        users.put(user.getPesel(), user);
    }

    public void addBook(Book book) {
        addPublication(book);
    }

    public void addMagazine(Magazine magazine) {
        addPublication(magazine);
    }

    public void addPublication(Publication publication) {
        if(publications.containsKey(publication.getTitle()))
            throw new PublicationAlreadyExistsException(
                    "Publikacja o takim tytule już istnieje " + publication.getTitle()
            );
        publications.put(publication.getTitle(), publication);
    }

    public boolean removePublication(Publication publication) {
        if(publications.containsValue(publication)) {
            publications.remove(publication.getTitle());
            return true;
        } else {
            return false;
        }
    }
}