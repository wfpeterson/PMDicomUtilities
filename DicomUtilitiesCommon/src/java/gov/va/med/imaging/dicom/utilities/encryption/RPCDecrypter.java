package gov.va.med.imaging.dicom.utilities.encryption;


public class RPCDecrypter {

	public void main(String args[]) {

		String decryptMessage = null;
		
		String encryptedMessage = args[0];
		
		decryptMessage = EncryptionUtils.decrypt(encryptedMessage);
		
		System.out.println("Decrypted Message = "+ decryptMessage);
		
	}
}
