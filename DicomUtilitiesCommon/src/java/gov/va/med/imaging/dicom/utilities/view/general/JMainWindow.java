/*
 * Created on Jan 3, 2006
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
public class JMainWindow extends JFrame{ //extends ApplicationWindow {

    private static JMainWindow APP;
    Display display = new Display();
    Shell shell = new Shell(display);

    private SingleImageContainer singleImageContainer;
    private SingleTGAImageContainer singleTgaImageContainer;
    
    private Action singleTgaImageAction;   
    private Action singleimageAction;
    private Action aboutAction;
    private Action exitAction;
    private Action searchAction;
    private Action compareAction;
    private Action anonymizeAction;
    private Action openAction;
    
    public JMainWindow() {
        //super(null);
        APP = this;
        //createActions();
        //addToolBar(SWT.FLAT | SWT.WRAP);
        //addMenuBar();
        //addStatusLine();
    }
    /*
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBounds(parent.getBounds());
        
        singleImageContainer = new SingleImageContainer(container, container.getStyle());
        singleImageContainer.setBounds(container.getBounds());
        singleImageContainer.setVisible(true);
        
        singleTgaImageContainer = new SingleTGAImageContainer(container, container.getStyle());
        singleTgaImageContainer.setBounds(container.getBounds());
        singleTgaImageContainer.setVisible(false);
        
        return container;
    }

    private void createActions() {

        openAction = new Action("Open") {
            public void run() {
                
            }
        };

        anonymizeAction = new Action("Anonymize") {
            public void run() {
                
                //this.simpleContainer.hideContainer();
                
            }
        };

        compareAction = new Action("Compare") {
            public void run() {
                //this.simpleContainer.hideContainer();
            }
        };

        searchAction = new Action("Search") {
            public void run() {
            }
        };

        exitAction = new Action("Exit") {
            public void run() {
                close();
            }
        };

        aboutAction = new Action("About") {
            public void run() {
            }
        };

        singleimageAction = new Action("Single Image") {
            public void run() {
               JMainWindow.getApp().showSimpleImageContainer();
            }
        };

        singleTgaImageAction = new Action("Single TGA Image") {
            public void run() {
                JMainWindow.getApp().showSimpleTGAImageContainer();
            }
        };

    }
    */

    /*
    protected MenuManager createMenuManager() {
        MenuManager result = new MenuManager("menu");

        final MenuManager fileMenu = new MenuManager("File");
        result.add(fileMenu);

        fileMenu.add(openAction);

        fileMenu.add(searchAction);

        fileMenu.add(new Separator());

        fileMenu.add(exitAction);

        final MenuManager viewMenu = new MenuManager("View");
        result.add(viewMenu);

        viewMenu.add(singleimageAction);

        viewMenu.add(singleTgaImageAction);

        viewMenu.add(compareAction);

        viewMenu.add(anonymizeAction);

        final MenuManager helpMenu = new MenuManager("Help");
        result.add(helpMenu);

        helpMenu.add(aboutAction);
        return result;
    }
    */
/**
    protected ToolBarManager createToolBarManager(int style) {
        ToolBarManager toolBarManager = new ToolBarManager(style);
        return toolBarManager;
    }

    protected StatusLineManager createStatusLineManager() {
        StatusLineManager statusLineManager = new StatusLineManager();
        statusLineManager.setMessage(null, "");
        return statusLineManager;
    }

    public static void main(String args[]) {
        try {
            JMainWindow window = new JMainWindow();
            //window.setBlockOnOpen(true);
            //window.open();
            Display.getCurrent().dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void configureShell(Shell newShell) {
        //super.configureShell(newShell);
        newShell.setText("DICOM Utilities");
    }

    protected Point getInitialSize() {
        return new Point(930, 410);
    }
    
    public static final JMainWindow getApp(){
        return APP;
    }
    
    public void showSimpleImageContainer(){
        this.singleTgaImageContainer.setVisible(false);
        this.singleImageContainer.setVisible(true);
        
    }
    
    public void showSimpleTGAImageContainer(){
        this.singleImageContainer.setVisible(false);
        this.singleTgaImageContainer.setVisible(true);
    }
}
**/
