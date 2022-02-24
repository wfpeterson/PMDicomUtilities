package gov.va.med.imaging.dicom.utilities.model;

import java.io.File;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import gov.va.med.imaging.dicom.utilities.exceptions.DataCreationException;
import gov.va.med.imaging.dicom.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.PixelDataException;
import gov.va.med.imaging.dicom.utilities.exceptions.ValidationException;

public interface IHistogramDataControl {

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#openFile(java.lang.String)
	 */
	void openFile(String dcmFile) throws DicomFileException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#openFile(java.io.File)
	 */
	void openFile(File dcmFile) throws DicomFileException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogram()
	 */
	IHistogramData getHistogram() throws PixelDataException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#validateIOD()
	 */
	String validateIOD() throws ValidationException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getMetaData()
	 */
	Vector getMetaData() throws DataCreationException;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getMetaDataHeaderNames()
	 */
	Vector getMetaDataHeaderNames() throws DataCreationException;

	TableModel getDataSetTableModel();

	JTable getLoadedDataSetJTree();

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogramTable()
	 */
	Vector getHistogramTable();

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utils.pixelmed.model.interfaces.IDicomInformation#getHistogramTableNames()
	 */
	Vector getHistogramTableNames();

}