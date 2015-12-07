package com.uzh.gwt.softeng.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class FileUploader extends Composite {
	
	HorizontalPanel panel = new HorizontalPanel();
	private FormPanel form = new FormPanel();
	private FileUpload fu = new FileUpload();
	Label selectLabel = new Label("Select a file:");
    Button uploadButton = new Button("Upload File");
    private String baseUrl = GWT.getModuleBaseURL();
    
    FileUploader() {
    	form.setAction(baseUrl + "filmInfo");
    	form.setEncoding(FormPanel.ENCODING_MULTIPART);
    	form.setMethod(FormPanel.METHOD_POST);
    	
    	fu.setName("fileUpload");
    	panel.add(selectLabel);
    	panel.add(fu);
    	panel.add(uploadButton);
    	
    	uploadButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//get the filename to be uploaded
	            String filename = fu.getFilename();
	            if (filename.length() == 0) {
	               Window.alert("No File Specified!");
	            } else {
	               //submit the form
	               form.submit();
	               
	            }			
			}
    		
    	});
    	
    	form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
               Window.alert("File upload completed!");				
            }
         });
         panel.setSpacing(10);
   	      
         form.add(panel);      
         
         initWidget(form);
    }
}
