import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.management.JMException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
class FileOperation
{
    Notepad npd;
    boolean saved;
    boolean newfileflag;
    String filename;
    File fileref;
    String applicationname="Notepad";
    JFileChooser chooser;
    FileOperation(Notepad npd)
    {
        this.npd=npd;
        saved=true;
        newfileflag=true;
        filename="Untitled";
        fileref=new File(filename);
        this.npd.frame.setTitle(filename+"-"+applicationname);
        chooser=new JFileChooser(".");
    }


    boolean saveFile(File temp) {
        FileWriter fout = null;
        BufferedWriter bout=null;
        try {
            fout = new FileWriter(temp);
            bout=new BufferedWriter(fout);
            bout.write(npd.ta.getText());

        } catch (IOException e) {
            updatestatus(temp, false);
            return false;
        } finally {
            try {
                fout.close();
            } catch (IOException ev) {
            }
        }
        updatestatus(temp,true);
        return true;
    }
    void updatestatus(File temp,boolean saved)
    {
        if(saved)
        {
            filename=temp.getPath();
            if(!temp.canWrite())
            {
                filename+="(Read only)";
                newfileflag=true;
            }
            fileref=temp;
            npd.frame.setTitle(filename+"-"+applicationname);
            npd.statusbar.setText("File : "+temp.getPath()+"saved/opened successfully.");
            newfileflag=false;
        }
        else
        {
            npd.statusbar.setText("Failed to open/save  "+temp.getPath());
        }
    }
    boolean saveAsFile()
    {
        File temp=null;
        chooser.setDialogTitle("Save as...");
        chooser.setApproveButtonText("save now");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        chooser.setApproveButtonToolTipText("click me to save");
        do {
            if(chooser.showSaveDialog(npd.frame)!=JFileChooser.APPROVE_OPTION)
                return false;
            temp=chooser.getSelectedFile();
            if(!temp.exists())break;
            if(JOptionPane.showConfirmDialog(npd.frame,"<html>"+temp.getPath()+" already exists.<br>Do you want to replace it?<html>",
                    "Save As",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                break;

        }while(true);
        return saveFile(temp);
    }
    boolean saveThisFile()
    {
        if(!newfileflag)
           return saveFile(fileref);
      return  saveThisFile();
    }
    void newFile()
    {
        if(!confirmsave()) return ;
        filename="Untitled";
        fileref=new File(filename);
        saved=true;
        newfileflag=true;
        npd.frame.setTitle(filename+"-"+applicationname);
        npd.ta.setText("");
    }
    boolean confirmsave()
    {
        String msg="the text in the "+filename+"has been changed do you want to save the channge?";
        if(!saved)
        {
            int x=JOptionPane.showConfirmDialog(npd.frame,msg,applicationname,JOptionPane.YES_NO_CANCEL_OPTION);
            if(x==JOptionPane.CANCEL_OPTION) return false;
            if(x==JOptionPane.YES_OPTION&&!saveAsFile()) return false;
        }
        return true;
    }
    boolean openFile(File temp)
    {
        BufferedReader br=null;
        FileReader fr=null;
        try{
           fr=new FileReader(temp);
            br=new BufferedReader(fr);
            String s="";
            while(true)
            {
                s=br.readLine();
                if(s!=null)
                    break;
                npd.ta.append(s+"\n");
            }


        }catch(IOException e){
            updatestatus(temp,false);
            return false;
        }
        finally {
            try{br.close();fr.close();}catch (IOException e){}
        }


        updatestatus(temp,true);
        return true;
    }

    void openFile()
    {
        if(!confirmsave()) return ;
        chooser.setDialogTitle("open As");
        chooser.setApproveButtonText("open this");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        chooser.setApproveButtonToolTipText("click me to open this file");
        if(chooser.showOpenDialog(npd.frame)!=JFileChooser.APPROVE_OPTION)
            return ;
        File temp=chooser.getSelectedFile();
        npd.ta.setText("");
        if(!openFile(temp))
        {
            filename="Untitled";
            saved=true;
            npd.frame.setTitle(filename+"-"+applicationname);
        }
    }





}


public class Notepad implements ActionListener, MenuConstants
{
    JFrame frame;
    JTextArea ta;
    String filename="Untitled";
    String applicationname="Notepad";
    FileOperation filehandler;
    JLabel statusbar=new JLabel("||    Ln 1 | coln 1",JLabel.RIGHT);

    ///////////////////////
    public Notepad() {
        frame = new JFrame(filename + "-" + applicationname);
        ta = new JTextArea(30, 60);
        frame.add(ta, BorderLayout.CENTER);
        frame.add(statusbar, BorderLayout.SOUTH);
        frame.add(new JLabel("  "), BorderLayout.EAST);
        frame.add(new JLabel("  "), BorderLayout.WEST);
        createMenuBar();
        filehandler=new FileOperation(this);
        frame.setSize(300,300);
        frame.setVisible(true);
        ta.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int pos=0,line=0,column=0;
                    try {
                         pos = ta.getCaretPosition();
                         line = ta.getLineOfOffset(pos);
                         column = pos - ta.getLineStartOffset(line);
                    }catch(Exception ev){}
                    if(ta.getText().length()==0) {
                        line = 0;
                        column=0;
                    }
                    else
                    statusbar.setText("||   Ln "+(line+1)+"  col "+(column+1));
            }
        });

        ta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
               filehandler.saved=false;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filehandler.saved=false;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filehandler.saved=false;
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    void gotoLine()
    {
        try{
            int linenumber=0;
            linenumber=ta.getLineOfOffset(ta.getCaretPosition());
            String temp=JOptionPane.showInputDialog(frame,"enter the linenumber"," "+linenumber);
            if(temp==null)
                return ;
            linenumber=Integer.parseInt(temp);
            ta.setCaretPosition(ta.getLineStartOffset(linenumber));
        }catch(Exception e){}
    }
    JMenuItem createMenuItem(String s,JMenu menu,ActionListener al)
    {
        JMenuItem temp=new JMenuItem(s);
        menu.add(temp);
        temp.addActionListener(al);
        return temp;

    }

    JMenuItem createMenuItem(String s,JMenu menu,int key,ActionListener al)
    {
        JMenuItem temp=new JMenuItem(s);
        temp.setAccelerator(KeyStroke.getKeyStroke(key,ActionEvent.CTRL_MASK));
        menu.add(temp);
        temp.addActionListener(al);
        return temp;

    }
    JMenu createMenu(String s,JMenuBar mb){
        JMenu temp=new  JMenu(s);
        mb.add(temp);
        return temp;
    }

    JMenu createMenu(String s,int key,JMenuBar mb){
        JMenu temp=new  JMenu(s);
        temp.setMnemonic(key);
        mb.add(temp);
        return temp;
    }

    void createMenuBar()
    {
        JMenuBar mb=new JMenuBar();
        JMenu file=createMenu(filemenu, KeyEvent.VK_F,mb);
        createMenuItem(filenew,file,KeyEvent.VK_N,this);
        createMenuItem(fileopen,file,KeyEvent.VK_O,this);
        createMenuItem(filesave,file,KeyEvent.VK_S,this);
        createMenuItem(filesaveas,file,KeyEvent.VK_A,this);
        file.addSeparator();
        createMenuItem(fileprint,file,KeyEvent.VK_P,this);
        createMenuItem(fileexit,file,KeyEvent.VK_Q,this);


        JMenu edit=createMenu(editmenu,KeyEvent.VK_E,mb);
        createMenuItem(editundo,edit,KeyEvent.VK_Z,this);
        createMenuItem(editcut,edit,KeyEvent.VK_X,this);
        createMenuItem(editcopy,edit,KeyEvent.VK_C,this);
        createMenuItem(editpaste,edit,KeyEvent.VK_V,this);
        createMenuItem(editdelete,edit,KeyEvent.VK_D,this);
        edit.addSeparator();
        createMenuItem(editfind,edit,KeyEvent.VK_F,this);
        createMenuItem(editfindnext,edit,KeyEvent.VK_F3,this);
        createMenuItem(editreplace,edit,KeyEvent.VK_H,this);
        createMenuItem(editgoto,edit,KeyEvent.VK_G,this);
        edit.addSeparator();
        createMenuItem(editselectall,edit,KeyEvent.VK_A,this);
        createMenuItem(edittimedate,edit,KeyEvent.VK_F5,this);

        JMenu formate=createMenu(formatemenu,mb);

        JMenu view=createMenu(viewmenu,mb);
//        createMenuItem(viewstatusbar,view,this);
        JCheckBoxMenuItem status=new JCheckBoxMenuItem("Status");
//        status.addActionListener(this);
        status.setSelected(true);
        status.addActionListener(this);

        view.add(status);

        JMenu help=createMenu(helpmenu,mb);
        createMenuItem(helpabout,help,this);
        frame.setJMenuBar(mb);
    }



    public void actionPerformed(ActionEvent ev)
    {
        String cmd=ev.getActionCommand();
        if(cmd.equals(filenew)) {
            filehandler.newFile();
        }
        else if(cmd.equals(fileopen))
            filehandler.openFile();
        else if(cmd.equals(filesave))
            filehandler.saveThisFile();
        else if(cmd.equals(filesaveas))
            filehandler.saveAsFile();
        else if(cmd.equals(fileprint))
        {
            String msg="this command is yet to be implemented";
            JOptionPane.showMessageDialog(frame,msg);
        }
        else if(cmd.equals(fileexit))
        {
            if(filehandler.confirmsave())
               System.exit(0);
        }
        else if(cmd.equals(editundo))
        {
            String msg="this command is yet to be implemented";
            JOptionPane.showMessageDialog(frame,msg);
        }
        else if(cmd.equals(editcut))
            ta.cut();
        else if(cmd.equals(editcopy))
            ta.copy();
        else if(cmd.equals(editpaste))
            ta.paste();
        else if(cmd.equals(editdelete))
            ta.replaceSelection("");
        else if(cmd.equals(editfind)||cmd.equals(editfindnext)||cmd.equals(editreplace))
        {
            String msg="this command is yet to be implemented";
            JOptionPane.showMessageDialog(frame,msg);
        }
        else if(cmd.equals(editselectall))
            ta.selectAll();
        else if(cmd.equals(editgoto))
        {
            String msg="this command is yet to be implemented";
            JOptionPane.showMessageDialog(frame,msg);
//            gotoLine();
        }
        else if(cmd.equals(edittimedate))
        {

        }
        else if(cmd.equals(viewstatusbar))
        {
//            JMenuItem temp=(JMenuItem) ev.getSource();
            JCheckBoxMenuItem temp=(JCheckBoxMenuItem) ev.getSource();
           statusbar.setVisible(temp.isSelected());
           System.out.println("item changed");

        }
    }

    public static void main(String[] args)
    {
        Notepad npd=new Notepad();

    }
}

interface MenuConstants
{
   final String filemenu="File";
   final String filenew="New";
   final String filesave="Save";
   final String filesaveas="Save As";
   final String fileopen="Open";
   final String fileprint="Print";
   final String fileexit="Exit";

   final String editmenu="Edit";
   final String editundo="Undo";
   final String editcut="Cut";
   final String editcopy="copy";
   final String editpaste="paste";
   final String editdelete="delete";
   final String editfind="find";
   final String editreplace="replace";
   final String editfindnext="find next";
   final String editgoto="goto";
   final String editselectall="selectall";
   final String edittimedate="time/date";

   final String formatemenu="Formate";

   final String viewmenu="View";
   final String viewstatusbar="Status";

   final String helpmenu="Help";
   final String helpabout="about";
}


