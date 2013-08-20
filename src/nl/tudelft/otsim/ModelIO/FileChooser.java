package nl.tudelft.otsim.ModelIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import nl.tudelft.otsim.GUI.WED;
import static javax.swing.GroupLayout.Alignment.*;

public class FileChooser extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton[] fileButton = new JButton[20];
    private JTextField[] textField = new JTextField[20];
    private JLabel[] label = new JLabel[20]; 
    private String moduleName;
    private String command;

	public FileChooser(int files, String[] labels, String[] fileNames, String[] action) {
        for (int i = 0; i < files; i++ ) {
        	//fileCount++;
        	fileButton[i] = new JButton(fileNames[i]);
        	fileButton[i].setToolTipText(fileNames[i]);
        	fileButton[i].addActionListener(this);
        	fileButton[i].setActionCommand(action[i]);
        	fileButton[i].setEnabled(true);
        	textField[i] = new JTextField();
        	label[i] = new JLabel(labels[i]);
        }
        for (int i = files; i < 20; i++ )  {
        	fileButton[i] = new JButton("empty");
            fileButton[i].setVisible(false);
            textField[i] = new JTextField();
            textField[i].setVisible(false);
            label[i] = new JLabel();
            label[i].setVisible(false);
        }

        // remove redundant default border of check boxes - they would hinder
        // correct spacing and aligning (maybe not needed on some look and feels)

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup( layout.createParallelGroup(LEADING)
                	.addComponent(label[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[2], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[3], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[4], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                 	.addComponent(label[5], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[6], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[7], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[8], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[9], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[10], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                 	.addComponent(label[11], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[12], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[13], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[14], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[15], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[16], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                 	.addComponent(label[16], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[17], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                   	.addComponent(label[18], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
                	.addComponent(label[19], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
          	)
            .addGroup( layout.createParallelGroup(LEADING)
                .addComponent(textField[0], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[1], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[2], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[3], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[4], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[5], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[6], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[7], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[8], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[9], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[10], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[11], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[12], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[13], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[14], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[15], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[16], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[17], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[18], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField[19], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
            .addGroup( layout.createParallelGroup(LEADING)
                .addComponent(fileButton[0], 10, 30, 100)
                .addComponent(fileButton[1], 10, 30, 100)
                .addComponent(fileButton[2], 10, 30, 100)
                .addComponent(fileButton[3], 10, 30, 100)
                .addComponent(fileButton[4], 10, 30, 100)
                .addComponent(fileButton[5], 10, 30, 100)
                .addComponent(fileButton[6], 10, 30, 100)
                .addComponent(fileButton[7], 10, 30, 100)
                .addComponent(fileButton[8], 10, 30, 100)
                .addComponent(fileButton[9], 10, 30, 100)
                .addComponent(fileButton[10], 10, 30, 100)
                .addComponent(fileButton[11], 10, 30, 100)
                .addComponent(fileButton[12], 10, 30, 100)
                .addComponent(fileButton[13], 10, 30, 100)
                .addComponent(fileButton[14], 10, 30, 100)
                .addComponent(fileButton[15], 10, 30, 100)
                .addComponent(fileButton[16], 10, 30, 100)
                .addComponent(fileButton[17], 10, 30, 100)
                .addComponent(fileButton[18], 10, 30, 100)
                .addComponent(fileButton[19], 10, 30, 100)
                )
        );
       
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(label[0])
                .addComponent(textField[0], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[0], 25, 25, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(label[1])
                .addComponent(textField[1], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[1],  10, 10, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(label[2])
                .addComponent(textField[2], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[2],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(label[3])
                .addComponent(textField[3], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[3], 10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            	.addComponent(label[4])
            	.addComponent(textField[4], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[4],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            	.addComponent(label[5])
            	.addComponent(textField[5], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[5],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(label[6])
            	.addComponent(textField[6], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[6],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            	.addComponent(label[7])
            	.addComponent(textField[7], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[7],  10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(label[8])
            	.addComponent(textField[8], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[8],  10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[9])
            	.addComponent(textField[9], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[9], 10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[10])
            	.addComponent(textField[10], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[10],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[11])
            	.addComponent(textField[11], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[11], 10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[12])
            	.addComponent(textField[12], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[12],  10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[13])
            	.addComponent(textField[13], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[13],  10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[14])
            	.addComponent(textField[14], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[14],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[15])
            	.addComponent(textField[15], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[15],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[16])
            	.addComponent(textField[16], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[16],  10, 20, 25)
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[17])
            	.addComponent(textField[17], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[17],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[18])
            	.addComponent(textField[18], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[18],  10, 20, 25) 
                )
            .addGroup(layout.createParallelGroup(LEADING)
            		.addComponent(label[19])
            	.addComponent(textField[19], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(fileButton[19],  10, 20, 25) 
                )
        );
    }
	
	public JButton[] getFileButton() {
		return fileButton;
	}
	
	public JTextField[] getTextField() {
		return textField;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		command = e.getActionCommand();
    	// The module name is set in the classes the use this FileChooser 
		// If not done: no action!!!!
		System.out.println("actionperformed: command is " + command);
		if (moduleName == "ImportModelShape")  {
			try {
				ImportModelShapeWizard.importModel();
			} catch (Exception e1) {
				WED.showProblem(WED.ENVIRONMENTERROR, "Error loading model:\r\n%s", 
						WED.exeptionStackTraceToString(e1));
				e1.printStackTrace();
			}
		}
		
		if (moduleName == "LoadModel")  {
			try {
				LoadModel.loadModel();
			} catch (Exception e1) {
				WED.showProblem(WED.ENVIRONMENTERROR, "Error loading model:\r\n%s", 
						WED.exeptionStackTraceToString(e1));
				e1.printStackTrace();
			}
		}
		
		if (moduleName == "SaveModel")  {
			try {
				SaveModel.saveModel();
			} catch (Exception e1) {
				WED.showProblem(WED.ENVIRONMENTERROR, "Error saving model:\r\n%s", 
						WED.exeptionStackTraceToString(e1));
				e1.printStackTrace();
			}
		}
		
		if (moduleName == "ExportModel")  {
			try {
				ExportModel.exportModel();
			} catch (Exception e1) {
				WED.showProblem(WED.ENVIRONMENTERROR, "Error exporting model:\r\n%s", 
						WED.exeptionStackTraceToString(e1));
				e1.printStackTrace();
			}
		}
	}

    public String getCommand() {
		return command;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
}