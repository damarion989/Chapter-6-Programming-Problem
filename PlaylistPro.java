import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

class Song {
    String title;
    String artist;
    int duration;

    public Song(String title, String artist, int duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return title + " by " + artist + " (" + duration + " seconds)";
    }
}

class SinglyLinkedList implements Iterable<Song> {
    // Protected so the sorted subclass can access nodes.
    protected static class Node {
        Song data;
        Node next;

        Node(Song data) {
            this.data = data;
        }
    }

    protected Node head;

    public void addLast(Song song) {
        Node newNode = new Node(song);

        if (head == null) {
            head = newNode;
            return;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    public void display() {
        if (head == null) {
            System.out.println("(empty playlist)");
            return;
        }

        Node current = head;

        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }

    public Song get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node current = head;
        int position = 0;

        while (current != null && position < index) {
            current = current.next;
            position++;
        }

        if (current == null) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        return current.data;
    }

    public void add(int index, Song song) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node newNode = new Node(song);

        if (index == 0) {
            newNode.next = head;
            head = newNode;
            return;
        }

        Node previous = head;
        int position = 0;

        while (previous != null && position < index - 1) {
            previous = previous.next;
            position++;
        }

        if (previous == null) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        newNode.next = previous.next;
        previous.next = newNode;
    }

    @Override
    public Iterator<Song> iterator() {
        return new Iterator<Song>() {
            private Node nextNode = head;
            private Node previous = null;
            private Node lastReturned = null;
            private Node beforeLastReturned = null;
            private boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public Song next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more songs");
                }

                beforeLastReturned = previous;
                lastReturned = nextNode;
                previous = nextNode;
                nextNode = nextNode.next;
                canRemove = true;

                return lastReturned.data;
            }

            @Override
            public void remove() {
                if (!canRemove) {
                    throw new IllegalStateException(
                        "Call next() before each remove()"
                    );
                }

                if (beforeLastReturned == null) {
                    head = nextNode;
                } else {
                    beforeLastReturned.next = nextNode;
                }

                previous = beforeLastReturned;
                lastReturned = null;
                canRemove = false;
            }
        };
    }
}

class SortedLinkedList extends SinglyLinkedList {
    private final Comparator<Song> comparator;

    public SortedLinkedList(Comparator<Song> comparator) {
        this.comparator = comparator;
    }

    public void add(Song song) {
        Node newNode = new Node(song);

        if (head == null || comparator.compare(song, head.data) < 0) {
            newNode.next = head;
            head = newNode;
            return;
        }

        Node current = head;

        while (current.next != null
                && comparator.compare(current.next.data, song) <= 0) {
            current = current.next;
        }

        newNode.next = current.next;
        current.next = newNode;
    }

    @Override
    public void add(int index, Song song) {
        // Ignore the index to preserve sorted order.
        add(song);
    }

    @Override
    public void addLast(Song song) {
        // Preserve sorted order through this insertion method too.
        add(song);
    }
}

public class PlaylistPro {
    public static void main(String[] args) {
        Song first = new Song("Night Drive", "Nova", 210);
        Song second = new Song("Blue Sky", "Atlas", 180);
        Song third = new Song("After Hours", "Echo", 240);

        System.out.println("PHASE 1: Basic Playlist");
        SinglyLinkedList playlist = new SinglyLinkedList();
        playlist.addLast(first);
        playlist.addLast(second);
        playlist.addLast(third);
        playlist.display();

        System.out.println("\nPHASE 2: For-each Loop");
        for (Song song : playlist) {
            System.out.println(song);
        }

        Iterator<Song> iterator = playlist.iterator();
        iterator.next();
        iterator.next();
        iterator.remove();

        System.out.println("\nAfter removing Blue Sky:");
        playlist.display();

        System.out.println("\nPHASE 3: Insert in the Middle");
        playlist.add(1, new Song("Golden Hour", "Sol", 200));
        playlist.display();
        System.out.println("Song at index 1: " + playlist.get(1));

        System.out.println("\nPHASE 4: Sorted by Title");
        Comparator<Song> byTitle = new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.title.compareToIgnoreCase(b.title);
            }
        };

        SortedLinkedList titleList = new SortedLinkedList(byTitle);
        titleList.add(first);
        titleList.add(third);
        titleList.add(second);
        titleList.display();

        System.out.println("\nSorted by Artist");
        Comparator<Song> byArtist = new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.artist.compareToIgnoreCase(b.artist);
            }
        };

        SortedLinkedList artistList = new SortedLinkedList(byArtist);
        artistList.add(third);
        artistList.add(first);
        artistList.add(second);
        artistList.display();

        System.out.println("\nPHASE 5: Edge Cases");
        SinglyLinkedList test = new SinglyLinkedList();

        System.out.println("\nEmpty list:");
        test.display();

        System.out.println("\nAdd to empty list:");
        test.addLast(first);
        test.display();

        System.out.println("\nInsert at index 0:");
        test.add(0, second);
        test.display();

        System.out.println("\nRemove first element:");
        Iterator<Song> testIterator = test.iterator();
        testIterator.next();
        testIterator.remove();
        test.display();

        System.out.println("\nRemove last remaining element:");
        testIterator.next();
        testIterator.remove();
        test.display();

        System.out.println("\nRemove last element of a longer list:");
        test.addLast(first);
        test.addLast(second);
        test.addLast(third);

        testIterator = test.iterator();
        while (testIterator.hasNext()) {
            testIterator.next();
        }
        testIterator.remove();
        test.display();

        System.out.println("\nRemove consecutive elements:");
        testIterator = test.iterator();
        while (testIterator.hasNext()) {
            testIterator.next();
            testIterator.remove();
        }
        test.display();

        System.out.println("\nInvalid get:");
        try {
            test.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\nInvalid insertion:");
        try {
            test.add(2, first);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\nRemove before next:");
        testIterator = test.iterator();
        try {
            testIterator.remove();
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\nNext on empty list:");
        try {
            testIterator.next();
        } catch (NoSuchElementException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\nSorted insertion ignores index:");
        titleList.add(99, new Song("Apple Tree", "Zara", 150));
        titleList.addLast(new Song("Daylight", "Milo", 190));
        titleList.display();
    }
}
