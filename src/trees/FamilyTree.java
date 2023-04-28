package trees;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class FamilyTree
{
    
    private static class TreeNode<T>
    {
        private T                    data;
        private TreeNode<T>               parent;
        private ArrayList<TreeNode<T>>        children;
        
        
        TreeNode(T name)
        {
            this.data = name;
            children = new ArrayList<>();
        }
        
        
        T getData()
        {
            return data;
        }
        
        
        void addChild(TreeNode<T> childNode)
        {
            // Add childNode to this node's children list. Also
            // set childNode's parent to this node.
        	childNode.parent = this;
        	this.children.add(childNode);
        	
        }
        
        
        // Searches subtree at this node for a node
        // with the given name. Returns the node, or null if not found.
        TreeNode<T> getNodeWithData(T targetName)
        {
            // Does this node have the target name?
            if (this.data.equals(targetName))
                return this;
                    
            // No, recurse. Check all children of this node.
            for (TreeNode<T> child: children)
            {
                // If child.getNodeWithName(targetName) returns a non-null node,
                // then that's the node we're looking for. Return it.
            	if (child.getNodeWithData(targetName)!= null) {
            		return child;
            	}
            }
            
            // Not found anywhere.
            return null;
        }
        
        
        // Returns a list of ancestors of this TreeNode, starting with this node’s parent and
        // ending with the root. Order is from recent to ancient.
        ArrayList<TreeNode<T>> collectAncestorsToList()
        {
            ArrayList<TreeNode<T>> ancestors = new ArrayList<TreeNode<T>>();

            // ?????  Collect ancestors of this TreeNode into the array list. HINT: going up
            // the nodes of a tree is like traversing a linked list. If that isn’t clear,
            // draw a tree, mark any leaf node, and then mark its ancestors in order from
            // recent to ancient. Expect a question about this on the final exam.
            TreeNode<T> currentNode = this.getNodeWithData (this.data);
            while (currentNode.parent!= null) {
            	ancestors.add(currentNode.parent);
            	currentNode = currentNode.parent;
            }

            return ancestors;
        }
        
        
        public String toString()
        {
            return toStringWithIndent("");
        }
        
        
        private String toStringWithIndent(String indent)
        {
            String s = indent + data + "\n";
            indent += "  ";
            for (TreeNode<T> childNode: children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

	private TreeNode<String>			root;
	
	
	//
	// Displays a file browser so that user can select the family tree file.
	//
	public FamilyTree() throws IOException, TreeException
	{
		// User chooses input file. This block doesn't need any work.
		FileNameExtensionFilter filter = 
			new FileNameExtensionFilter("Family tree text files", "txt");
		File dirf = new File("data");
		if (!dirf.exists())
			dirf = new File(".");
		JFileChooser chooser = new JFileChooser(dirf);
		chooser.setFileFilter(filter);
		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			System.exit(1);
		File treeFile = chooser.getSelectedFile();

		// Parse the input file. Create a FileReader that reads treeFile. Create a BufferedReader
		// that reads from the FileReader.
		FileReader fr = new FileReader(treeFile);
		BufferedReader br = new BufferedReader (fr);
		String line;
		while ((line = br.readLine()) != null)
			addLine(line);
		br.close();
		fr.close();
	}
	
	
	//
	// Line format is "parent:child1,child2 ..."
	// Throws TreeException if line is illegal.
	//
	private void addLine(String line) throws TreeException
	{
		// Extract parent and array of children.
		int colonIndex = line.indexOf(':');      //should be the index of the colon in line.
		if (colonIndex < 0)
		{ throw new TreeException("no colon shows"); }//with a useful message
		String parent = line.substring(0,colonIndex); // substring of line that starts at char #0 and ends just before colonIndex. Check the API for 
				           //class java.util.String, method substring(), if you need guidance.
		String childrenString = line.substring(colonIndex+1); //substring of line that starts just after colonIndex and goes through the end of
				                   //the line. You'll use a different version of substring().
		String[] childrenArray = childrenString.split(","); // Call childrenString.split(). Check the API for details. The result will be an array
				                    //of strings, with the separating commas thrown away.
		
		// Find parent node. If root is null then the tree is empty and the
		// parent node must be constructed. Otherwise the parent node should be 
		// somewhere in the tree.
		TreeNode<String> parentNode;
		if (root == null) {
			parentNode = new TreeNode<String>(parent);
			root = parentNode;
		}
		else
		{
			parentNode = root.getNodeWithData(parent);  // There's a method in Node that searches for a named node. 
			//??? If the parent node wasn't found, there must have been something wrong in the data file.Throw an exception.
			if (parentNode == null) {
				throw new TreeException("parent not found");
			}
		}
		
		// Add child nodes to parentNode.
		//?? For each name in childrenArray, create a new node and add that node to parentNode.
		for ( int i = 0; i< childrenArray.length;i++) {
			parentNode.addChild(new TreeNode<String>(childrenArray[i]));
		}
	}
	
	
	// Returns the "deepest" node that is an ancestor of the node named name1, and also is an
	// ancestor of the node named name2.
	//
	// "Depth" of a node is the "distance" between that node and the root. The depth of the root is 0. The
	// depth of the root's immediate children is 1, and so on.
	//
	TreeNode<String> getMostRecentCommonAncestor(String name1, String name2) throws TreeException
	{
		// Get nodes for input names.
		TreeNode<String> node1 = root.getNodeWithData(name1);//???		// node whose name is name1
		if (node1 == null) {
			throw new TreeException ("invalid name1");} //with a useful message
		TreeNode<String> node2 = root.getNodeWithData(name2);	// node whose name is name2
		if (node2 == null) {
			throw new TreeException("invalid name2");}//with a useful message
		
		// Get ancestors of node1 and node2.
		ArrayList<TreeNode<String>> ancestorsOf1 = node1.collectAncestorsToList();
		ArrayList<TreeNode<String>> ancestorsOf2 = node2.collectAncestorsToList();
		
		// Check members of ancestorsOf1 in order until you find a node that is also
		// an ancestor of 2. 
		for (TreeNode<String> n1: ancestorsOf1) {
			if (ancestorsOf2.contains(n1))
				return n1;
		}
		
		// No common ancestor.
		return null;
	}
	
	
	public String toString()
	{
		return "Family Tree:\n\n" + root;
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			FamilyTree tree = new FamilyTree();
			System.out.println("Tree:\n" + tree.toString() + "\n**************\n");
			TreeNode<String> ancestor = tree.getMostRecentCommonAncestor("Mungo", "Ponto");
			System.out.println("Most recent common ancestor of Mungo and Ponto is " + ancestor.getData());
		}
		catch (IOException x)
		{
			System.out.println("IO trouble: " + x.getMessage());
		}
		catch (TreeException x)
		{
			System.out.println("Input file trouble: " + x.getMessage());
		}
	}
}
