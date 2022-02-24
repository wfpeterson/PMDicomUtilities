/*
 * Created on Jan 2, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.sun.misc.treetable.TreeTableModel;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class SimpleFileSystemModel implements TreeTableModel {

    // Names of the columns.
    static protected String[]  cNames = {"Name", "Size", "Modified"};

    // Types of the columns.
    static protected Class[]  cTypes = { TreeTableModel.class,
                     Integer.class, Date.class};

    // The the returned file length for directories. 
    public static final Integer ZERO = new Integer(0);
    
    protected static File[] EMPTY_CHILDREN = new File[0];
    
    protected Object root;     
    
    protected Object[] shares;
    
    protected static final String myComputer = "COMPUTER";
    
    protected EventListenerList listenerList = new EventListenerList();

    

    /**
     * Constructor
     *
     * @param root
     */
    public SimpleFileSystemModel(Object root) {
        this.root = root;
    }

    /**
     * Constructor
     *
     * @param root
     */
    public SimpleFileSystemModel() {
        root = myComputer;
        //shares = File.listRoots();
        File[] tempVolumes = File.listRoots();
        Vector volumeList = new Vector();
        int totalVolumes = tempVolumes.length;
        for(int i=0; i<totalVolumes; i++){
            if(volumeExists(tempVolumes[i])){
                //Object keeper = (Object)tempVolumes[i];
                volumeList.add(tempVolumes[i]);
            }
        }
        //System.out.println("Number of valid shares: "+volumeList.size());
        shares = new File[volumeList.size()];
        for(int j=0; j<volumeList.size(); j++){
            shares[j] = volumeList.get(j);
        }
        //System.out.println("Available shares: "+volumeList.toString());
    }

    
    //
    //      IMPLEMENTATION OF TREETABLE MODEL
    //
    
    /* (non-Javadoc)
     * @see com.sun.misc.treetable.TreeTableModel#getColumnCount()
     */
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return cNames.length;
    }

    /* (non-Javadoc)
     * @see com.sun.misc.treetable.TreeTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return cNames[column];
    }
    
    /**
     * Returns the class for the particular column.
     */
    public Class getColumnClass(int column) {
    return cTypes[column];
    }


    /* (non-Javadoc)
     * @see com.sun.misc.treetable.TreeTableModel#getValueAt(java.lang.Object, int)
     */
    public Object getValueAt(Object node, int column) {
        if(node instanceof String){
            return null;
        }
        File fn = (File)node;
        
        try {
            switch(column) {
            case 0:
                return fn.getName();
            case 1:
                if(fn.isFile()){
                    return new Integer((int)(fn.length()));
                }
                return null;
            case 2:
                if(node instanceof String){
                    return null;
                }
                return new Date(fn.lastModified());
            }
        }
        catch  (SecurityException se) { }
        return null;
    }
    
    /** By default, make the column with the Tree in it the only editable one. 
     *  Making this column editable causes the JTable to forward mouse 
     *  and keyboard events in the Tree column to the underlying JTree. 
     */ 
     public boolean isCellEditable(Object node, int column) { 
          return getColumnClass(column) == TreeTableModel.class; 
     }

     public void setValueAt(Object aValue, Object node, int column) {}

     
     //
     //     IMPLEMENTATION OF TREE MODEL
     //
     
     
     public Object getRoot() {
         return root;
         //return myComputer;
     }

     public boolean isLeaf(Object node) {
         return getChildCount(node) == 0; 
     }

     /* (non-Javadoc)
      * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
      */
     public int getChildCount(Object parent) {
         File[] childrenFiles;
         if(parent instanceof String){
             childrenFiles = (File[])shares;
         }
         else{
         File fn = (File)parent;
         //File[] childrenFiles = fn.listFiles(new DCMFilenameFilter());
         childrenFiles = fn.listFiles();
         //String[] childrenFiles = fn.list();
         }
         if(childrenFiles == null){
             return 0;
         }
         else{
             return childrenFiles.length;
         }
     }

     /* (non-Javadoc)
      * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
      */
     public Object getChild(Object parent, int index) {
         //File fn = (File)parent;
         //File[] childrenFiles = fn.listFiles(new DCMFilenameFilter());
         //File[] childrenFiles = fn.listFiles();
         File[] childrenFiles;
         if(parent instanceof String){
             childrenFiles = (File[])shares;
             return (childrenFiles[index]);
         }
         else{
             File directory = (File) parent;
             childrenFiles = directory.listFiles();
             return new TreeFile(directory, childrenFiles[index].getName());
         }

         //String[] childrenFiles = fn.list();
         //return childrenFiles[index];
     }

     public void valueForPathChanged(TreePath path, Object newValue) {}

     // This is not called in the JTree's default mode: use a naive implementation. 
     public int getIndexOfChild(Object parent, Object child) {
         for (int i = 0; i < getChildCount(parent); i++) {
         if (getChild(parent, i).equals(child)) { 
             return i; 
         }
         }
     return -1; 
     }
     
     public void addTreeModelListener(TreeModelListener l) {
         listenerList.add(TreeModelListener.class, l);
     }

     public void removeTreeModelListener(TreeModelListener l) {
         listenerList.remove(TreeModelListener.class, l);
     }

     
     //
     //     FIRE LISTENERS
     //
     
     /*
      * Notify all listeners that have registered interest for
      * notification on this event type.  The event instance 
      * is lazily created using the parameters passed into 
      * the fire method.
      * @see EventListenerList
      */
     protected void fireTreeNodesChanged(Object source, Object[] path, 
                                         int[] childIndices, 
                                         Object[] children) {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         TreeModelEvent e = null;
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==TreeModelListener.class) {
                 // Lazily create the event:
                 if (e == null)
                     e = new TreeModelEvent(source, path, 
                                            childIndices, children);
                 ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
             }          
         }
     }

     /*
      * Notify all listeners that have registered interest for
      * notification on this event type.  The event instance 
      * is lazily created using the parameters passed into 
      * the fire method.
      * @see EventListenerList
      */
     protected void fireTreeNodesInserted(Object source, Object[] path, 
                                         int[] childIndices, 
                                         Object[] children) {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         TreeModelEvent e = null;
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==TreeModelListener.class) {
                 // Lazily create the event:
                 if (e == null)
                     e = new TreeModelEvent(source, path, 
                                            childIndices, children);
                 ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
             }          
         }
     }

     /*
      * Notify all listeners that have registered interest for
      * notification on this event type.  The event instance 
      * is lazily created using the parameters passed into 
      * the fire method.
      * @see EventListenerList
      */
     protected void fireTreeNodesRemoved(Object source, Object[] path, 
                                         int[] childIndices, 
                                         Object[] children) {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         TreeModelEvent e = null;
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==TreeModelListener.class) {
                 // Lazily create the event:
                 if (e == null)
                     e = new TreeModelEvent(source, path, 
                                            childIndices, children);
                 ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
             }          
         }
     }

     /*
      * Notify all listeners that have registered interest for
      * notification on this event type.  The event instance 
      * is lazily created using the parameters passed into 
      * the fire method.
      * @see EventListenerList
      */
     protected void fireTreeStructureChanged(Object source, Object[] path, 
                                         int[] childIndices, 
                                         Object[] children) {
         // Guaranteed to return a non-null array
         Object[] listeners = listenerList.getListenerList();
         TreeModelEvent e = null;
         // Process the listeners last to first, notifying
         // those that are interested in this event
         for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==TreeModelListener.class) {
                 // Lazily create the event:
                 if (e == null)
                     e = new TreeModelEvent(source, path, 
                                            childIndices, children);
                 ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
             }          
         }
     }
     
     public File createFilePath(TreePath tp){
         
         File filepath;
         StringBuffer pathname = new StringBuffer("");
         int start=0;
         if(root instanceof String){
         start++;
         }
         for(int i=start; i<tp.getPathCount(); i++){
             boolean addSeparator = true;
             String temp = tp.getPathComponent(i).toString();
             if(pathname.toString().equals("")){
                 addSeparator = false;
             }
             if(pathname.toString().endsWith("\\")){
                 addSeparator = false;
             }
             if(addSeparator){
                 pathname.append(File.separator);                 
             }
             pathname.append(temp);
         }
         System.out.println("Completed Path: "+pathname);
         
         if(pathname.toString().equals("")){
             return null;
         }
         pathname.toString().trim();
         filepath = new File(pathname.toString());
         return filepath;
         //return null;
     }
     
    /**
     * Gives the same basic functionality of File.exists but can be
     * used to look for removable media without showing a system
     * dialog if the media is not present.
     */
    public static boolean volumeExists( File file )
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec( new String[] {"cmd.exe", "/c", "dir", file.getAbsolutePath()} );

            // We need to consume all available output or the process will block.
            boolean haveExitCode = false;
            int exitCode = -1;
            InputStream out = process.getInputStream();
            InputStream err = process.getErrorStream();
            
            while ( !haveExitCode )
            {
                while ( out.read() >= 0 )
                {
                }
                while ( err.read() >= 0 )
                {
                }
                
                try
                {
                    exitCode = process.exitValue();
                    haveExitCode = true;
                }
                catch ( IllegalThreadStateException e )
                {
                    // Not yet complete.
                    Thread.sleep( 100 );
                }
            }
            
            //int exitCode = process.waitFor();
            return exitCode == 0;
        }
        catch ( IOException e )
        {
            //System.out.println( "Unable to check for file: " + file + " : " + e );
            return false;
        }
        catch ( InterruptedException e )
        {
            //System.out.println( "Unable to check for file.  Interrupted: " + file + " : " + e );
            return false;
        }
    }     
     
     class TreeFile extends File {
         public TreeFile(File parent, String child) {
           super(parent, child);
         }

         public String toString() {
           return getName();
         }
     }     
}