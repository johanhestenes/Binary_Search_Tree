import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
   This class implements a binary search tree whose
   nodes hold objects that implement the Comparable
   interface.
*/
public class BinarySearchTree
{  
   private Node root;
   private int currentSize;

   /**
      Constructs an empty tree.
   */
   public BinarySearchTree()
   {  
      currentSize = 0;
      root = null;
   }
   
   public Iterator iterator()
   {
       return new BinarySearchTreeIterator(root);
   }
   
   public int numNodes()
   {
       return currentSize;
   }
   
   /**
      Inserts a new node into the tree.
      @param obj the object to insert
   */
   public void add(Comparable obj) 
   {  
      Node newNode = new Node();
      newNode.data = obj;
      newNode.left = null;
      newNode.right = null;
      if (root == null) 
      { 
          root = newNode; 
      }
      else 
      { 
          root.addNode(newNode); 
      }
      currentSize++;
   }

   /**
      Tries to find an object in the tree.
      @param obj the object to find
      @return true if the object is contained in the tree
   */
   public boolean find(Comparable obj)
   {
      Node current = root;
      while (current != null)
      {
         int d = current.data.compareTo(obj);
         if (d == 0) { return true; }
         else if (d > 0) { current = current.left; }
         else { current = current.right; }
      }
      return false;
   }
   
   /**
      Tries to remove an object from the tree. Does nothing
      if the object is not contained in the tree.
      @param obj the object to remove
   */
   public void remove(Comparable obj)
   {
      // Find node to be removed
      Node toBeRemoved = root;
      Node parent = null;
      boolean found = false;
      while (!found && toBeRemoved != null)
      {
         int d = toBeRemoved.data.compareTo(obj);
         if (d == 0) { found = true; }
         else 
         {
            parent = toBeRemoved;
            if (d > 0) { toBeRemoved = toBeRemoved.left; }
            else { toBeRemoved = toBeRemoved.right; }
         }
      }
      if (found)  { currentSize--; }
      if (!found) { return; }

      // toBeRemoved contains obj

      // If one of the children is empty, use the other

      if (toBeRemoved.left == null || toBeRemoved.right == null)
      {
         Node newChild;
         if (toBeRemoved.left == null) 
         {
            newChild = toBeRemoved.right;
         }
         else 
         {
            newChild = toBeRemoved.left;
         }

         if (parent == null) // Found in root
         {
            root = newChild;
         }
         else if (parent.left == toBeRemoved)
         {
            parent.left = newChild;
         }
         else 
         {
            parent.right = newChild;
         }
         return;
      }
      
      // Neither subtree is empty

      // Find smallest element of the right subtree

      Node smallestParent = toBeRemoved;
      Node smallest = toBeRemoved.right;
      while (smallest.left != null)
      {
         smallestParent = smallest;
         smallest = smallest.left;
      }

      // smallest contains smallest child in right subtree
         
      // Move contents, unlink child

      toBeRemoved.data = smallest.data;
      if (smallestParent == toBeRemoved) 
      {
         smallestParent.right = smallest.right; 
      }
      else 
      {
         smallestParent.left = smallest.right; 
      }
   }
   
   /**
      Prints the contents of the tree in sorted order.
   */
   public void print()
   {  
      print(root);
      System.out.println();
   }  

   /**
      Prints a node and all of its descendants in sorted order.
      @param parent the root of the subtree to print
   */
   private static void print(Node parent)
   {  
      if (parent == null) { return; }
      print(parent.left);
      System.out.print(parent.data + " ");
      print(parent.right);
   }

   /**
      A node of a tree stores a data item and references
      to the left and right child nodes.
   */
   class Node
   {  
      public Comparable data;
      public Node left;
      public Node right;

      /**
         Inserts a new node as a descendant of this node.
         @param newNode the node to insert
      */
      public void addNode(Node newNode)
      {  
         int comp = newNode.data.compareTo(data);
         if (comp < 0)
         {  
            if (left == null) { left = newNode; }
            else { left.addNode(newNode); }
         }
         else if (comp > 0)
         {  
            if (right == null) { right = newNode; }
            else { right.addNode(newNode); }
         }
      }
   }
   
   private class BinarySearchTreeIterator implements Iterator
   {
        private Stack<Node> stk;
        private Node current;
        private boolean canRemove;
        private int expectedSize = currentSize;
        
        public BinarySearchTreeIterator(Node root) 
        {
            if (root != null)
            {
                stk = new Stack<>();
                stk.push(root);
                while (root.left != null) 
                {
                    root = root.left;
                    stk.push(root);
                }
            }
            else throw new NoSuchElementException();
	}
        
        @Override
        public boolean hasNext() 
        {  
            if (expectedSize != currentSize)
            {
                throw new ConcurrentModificationException();
            }
            return !stk.isEmpty();
        }

        @Override
        public Object next() 
        {   
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            if (expectedSize != currentSize)
            {
                throw new ConcurrentModificationException();
            }
            canRemove = true;
            current = stk.pop();
	    Node node = current.right;
	    while (node != null) 
            {
		stk.push(node);
		node = node.left;
	    }
	    return current.data;
        }

        @Override
        public void remove() 
        { 
            if (canRemove == false)
            {
                throw new IllegalStateException();
            }
            if (expectedSize != currentSize)
            {
                throw new ConcurrentModificationException();
            }
            expectedSize--;
            BinarySearchTree.this.remove(current.data);
            canRemove = false;
        }   
   }
}

