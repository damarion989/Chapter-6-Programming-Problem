# Playlist Pro Journal

## Phase 1
I created Song and the linked list. Each node stores a song and
a reference to the next node instead of using array slots.

## Phase 2
Removing a node connects the previous node to the next one.
The iterator remembers its position so traversal can continue.

## Phase 3
Middle insertion is O(N) for both structures. A linked list
traverses nodes, while an array-based list shifts elements.

## Phase 4
A separate sorted list keeps songs ordered. A Comparator lets
the same class sort by title or artist.
