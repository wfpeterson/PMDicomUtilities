/*
 * Created on Jan 12, 2006
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
public class SingleTGAImageContainer extends Composite {

    private Canvas tgaCanvas;
    private Tree tree;
    private StyledText tgaValueText;
    private TgaImageContentProvider tgaContent;
    
    public SingleTGAImageContainer(Composite parent, int style) {
        super(parent, style);
        
        final SashForm sashForm = new SashForm(this, SWT.NONE);

        final TreeViewer tgaTreeViewer = new TreeViewer(sashForm, SWT.BORDER);
        tree = tgaTreeViewer.getTree();
        tree.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                File file;
                TreeItem[] selecteditem;
                int itemCount;
                itemCount = tgaTreeViewer.getTree().getSelectionCount();
                selecteditem = tgaTreeViewer.getTree().getSelection();
                file = (File)selecteditem[itemCount-1].getData();
                
                if(file.isFile()){
                    try{
                        tgaContent = new TgaImageContentProvider();
                        tgaContent.openFile(file);
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
        tgaTreeViewer.setLabelProvider(new TreeLabelProvider());
        tgaTreeViewer.setContentProvider(new TreeContentProvider());
        tgaTreeViewer.setInput("root");

        final TabFolder tgaTabFolder = new TabFolder(sashForm, SWT.NONE);

        final TabItem displayPixelValuesTabItem = new TabItem(tgaTabFolder, SWT.NONE);
        displayPixelValuesTabItem.setText("Display TGA Pixel Values");

        tgaValueText = new StyledText(tgaTabFolder, SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
        displayPixelValuesTabItem.setControl(tgaValueText);

        final TabItem graphPixelValuesTabItem = new TabItem(tgaTabFolder, SWT.NONE);
        graphPixelValuesTabItem.setText("Graph TGA Pixel Values");

        tgaCanvas = new Canvas(tgaTabFolder, SWT.NONE);
        tgaCanvas.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        graphPixelValuesTabItem.setControl(tgaCanvas);
        
        sashForm.setWeights(new int[] { 1, 1 });
        sashForm.setBounds(10, 10, 920, 400);
    }

    public void dispose() {
        super.dispose();
    }

    protected void checkSubclass() {
    }
    
    public void hideContainer(){
        this.setVisible(false);
    }
    
    public void showContainer(){
        this.setVisible(true);
    }
    public void update(){
        super.update();
        tgaValueText.setText(this.tgaContent.getPixelValues());
        tgaCanvas.setData(this.tgaContent);
        tgaCanvas.addPaintListener(new GraphPaintListener());
    }

}
**/
