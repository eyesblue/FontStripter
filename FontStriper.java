import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

public class FontStriper extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	JTextArea textArea=null;
	JLabel status=null;
	String desc="點選[選擇檔案]以多選您要處理的文字檔案。\n點選[選擇目錄]選擇您要處理的目錄與其下的所有目錄與檔案。\n點選[輸出FontForge命令稿]將文字輸出成命令稿。\n\n輸出成命令稿後，您可於FontForge中點選[File]->[Execute Script] -> [FF] -> [Call] 並選取輸出的命令稿，執行後將會從您所開啟的字型檔中去除其他您不要的字元，再由字型檔案內容產生字型檔即可。\n\n您可以在下面的文字區加入您要引入的文字或刪除不要的文字。";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FontStriper frame = new FontStriper();
					frame.setTitle("FontForge字型擷取命令稿產生器");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FontStriper() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(0, 3, 0, 0));
		
		JButton button = new JButton("選擇檔案");
		panel_1.add(button);
		button.setToolTipText("選擇單一個檔案，並解讀裡面的字元。");
		
		JButton button_1 = new JButton("選擇目錄");
		panel_1.add(button_1);
		button_1.setToolTipText("選擇一個目錄並解讀裡面所有的檔案，請注意！若此目錄下的檔案包含非文字檔，將可能造成不可預期的結果。");
		
		JButton btnfontforge = new JButton("輸出FontForge命令稿");
		panel_1.add(btnfontforge);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JTextArea descArea = new JTextArea(desc);
		descArea.setEditable(false);
		descArea.setLineWrap(true);
		panel_2.add(descArea);
		btnfontforge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();
				int ret=jfc.showSaveDialog(FontStriper.this);
				if(ret != JFileChooser.APPROVE_OPTION) return;
				
				
/*				for(int i=0;i<=33;i++){
					int index=sb.indexOf(""+(char)i);
					if(index==-1)continue;
					sb.deleteCharAt(index);
				}
*/				
				
				String text=textArea.getText();
				text.replace("\n", "").replace("\r", "");
				System.out.println("There are "+text.length()+" strings.");
				
				try {
					FileWriter fos = new FileWriter(jfc.getSelectedFile());
					Calendar c=Calendar.getInstance();
					fos.write("# Script for strip the glyph in font file that run in FontForge,\n# create by FontStriper at "+String.format("%s/%s/%s", c.get(Calendar.YEAR),(c.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH))+"; Author: Eyes Blue.\n");
					fos.write("SelectNone();\n");
					for(int i=0;i<text.length();i++)
						fos.write(String.format ("SelectMore(\"u%04x\");\n", (int)text.charAt(i)));
					fos.write("SelectInvert();\n");
					fos.write("DetachAndRemoveGlyphs();");
					fos.flush();
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					status.setText("發生錯誤！");
				}
			}
		});
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				int ret=jfc.showOpenDialog(FontStriper.this);
				
				if(ret != JFileChooser.APPROVE_OPTION) return;
				
				ArrayList<String> lists=new ArrayList<>();
				getFileNames(lists, jfc.getSelectedFile().toPath());
				File[] files=new File[lists.size()];
				for(int i=0;i<lists.size();i++)
					files[i]=new File(lists.get(i));
					
				new Thread(){
					@Override
					public void run(){
						processFiles(files);
					}
				}.start();
			}
		});
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("List files");
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setMultiSelectionEnabled(true);
				int ret=jfc.showOpenDialog(FontStriper.this);
				
				if(ret != JFileChooser.APPROVE_OPTION) {
					System.out.println("User cancel action");
					return;
				}
				
				File[] files=jfc.getSelectedFiles();
				System.out.println("User select "+files.length+" files.");
				
				new Thread(){
					@Override
					public void run(){
						processFiles(files);
					}
				}.start();
			}
		});

		
		status = new JLabel("狀態列");
		contentPane.add(status, BorderLayout.SOUTH);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
	
	private List<String> getFileNames(List<String> fileNames, Path dir) {
	    try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
	        for (Path path : stream) {
	        	System.out.println("Process file: "+path.getFileName());
	            if(path.toFile().isDirectory()) {
	                getFileNames(fileNames, path);
	            } else {
	                fileNames.add(path.toAbsolutePath().toString());
	                System.out.println(path.getFileName());
	            }
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	        status.setText("發生錯誤！");
	    }
	    return fileNames;
	} 
	
	public void processFiles(File[] files){
		status.setText("處理中...");
		StringBuffer sb=new StringBuffer(textArea.getText());
		try {
			for(File f:files){
				status.setText("Read "+f.getAbsolutePath());
				FileReader fr=new FileReader(f);
				
				int ch=0;
				while((ch=fr.read())!=-1){
					if(sb.indexOf(""+(char)ch)==-1)
						sb.append((char)ch);
				}
				System.out.println("Size of target: "+sb.length());
				fr.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			status.setText("發生錯誤！");
		}
		String allStr=sb.toString();
		char[] chars=allStr.toCharArray();
		Arrays.sort(chars);
		allStr=new String(chars);
		
		status.setText("共 "+allStr.length()+" 字");
		textArea.setText(allStr);
	}

}
