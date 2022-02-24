/*
 * Created on Jan 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.utilities.view.general;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
/**
public class SingleImageContainer extends Composite {

    private StyledText pixelDataValuesText;
    private StyledText simpleHeaderText;
    private Tree tree;
    private SingleImageDICOMUtility dcmUtil;

    
    public SingleImageContainer(Composite parent, int style) {
        super(parent, style);

        final SashForm sashForm = new SashForm(this, SWT.NONE);

        final TreeViewer treeViewer = new TreeViewer(sashForm, SWT.BORDER);
        tree = treeViewer.getTree();
        tree.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                File file;
                TreeItem[] selecteditem;
                int itemCount;
                itemCount = treeViewer.getTree().getSelectionCount();
                selecteditem = treeViewer.getTree().getSelection();
                file = (File)selecteditem[itemCount-1].getData();
                
                if(file.isFile()){
                    try{
                        dcmUtil = new SingleImageDICOMUtility();
                        dcmUtil.setup(file);
                        sashForm.getParent().update();
                        sashForm.getParent().redraw();
                    }
                    catch(FileOpenException noOpen){
                        System.out.println("Could not open the file: " + noOpen.getMessage());
                        //TODO Need to add Dialog for this.
                    }
                }
            }
        });
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.setInput("root");

        final TabFolder singleImageTabFolder = new TabFolder(sashForm, SWT.NONE);

        final TabItem simpleHeaderDumpTabItem = new TabItem(singleImageTabFolder, SWT.NONE);
        simpleHeaderDumpTabItem.setText("Simple Header Dump");

        simpleHeaderText = new StyledText(singleImageTabFolder, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
        simpleHeaderText.setEditable(false);
        simpleHeaderText.setFont(SWTResourceManager.getFont("Arial", 10, SWT.NONE));
        simpleHeaderDumpTabItem.setControl(simpleHeaderText);

        final TabItem pixelDataValuesTabItem = new TabItem(singleImageTabFolder, SWT.NONE);
        pixelDataValuesTabItem.setText("Pixel Data Values");

        pixelDataValuesText = new StyledText(singleImageTabFolder, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
        pixelDataValuesTabItem.setControl(pixelDataValuesText);
        
        
        //sashForm.setWeights(new int[] { 241, 691 });
        sashForm.setWeights(new int[] {1, 1});
        sashForm.setBounds(10, 10, 920, 400);
        //sashForm.setBounds(this.getBounds());
    }

    public void dispose() {
        super.dispose();
    }

    protected void checkSubclass() {
    }
    
    public void update(){
        super.update();
        simpleHeaderText.setText(this.dcmUtil.simpleStyleHeader());
    }

    public void hideContainer(){
        this.setVisible(false);
    }
    
    public void showContainer(){
        this.setVisible(true);
    }

}
**/