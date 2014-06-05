import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Ventana extends JFrame implements ActionListener {
	private JButton[] teclado;
	private String[] teclas = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"*", "0", "#" };
	private JTextField display;
	private JButton reset;

	JMenuBar menuPrincipal;
	private JMenu archivoMenu, movilMenu, otrosMenu;
	private JMenuItem grabarItem, leerItem, resetItem, salirItem, acercaDeItem;

	public Ventana() {
		this.setTitle("Movil");
		this.setLayout(null);
		this.setSize(170, 350);

		display = new JTextField();
		display.setSize(150, 25);
		display.setLocation(10, 10);
		display.setEditable(false);
		this.add(display);

		teclado = new JButton[12];
		for (int i = 0, x = 0, y = 0; i < teclado.length; i++, x++) {
			if (i % 3 == 0 && i != 0) {
				x = 0;
				y++;
			}
			teclado[i] = new JButton(teclas[i]);
			teclado[i].setSize(50, 50);
			teclado[i].setLocation(10 + x * 50, 40 + y * 50);
			teclado[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					display.setText(display.getText()
							+ ((JButton) arg0.getSource()).getText());
					((JButton) arg0.getSource()).setBackground(Color.BLUE);
					Ventana.this.requestFocus();
				}
			});
			teclado[i].addMouseListener(new EventosRaton());
			this.add(teclado[i]);
		}

		reset = new JButton("Reset");
		reset.setSize(100, 50);
		reset.setLocation(35, 250);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (JButton tecla : teclado) {
					tecla.setBackground(null);
					display.setText("");
				}
				Ventana.this.requestFocus();
			}
		});
		this.add(reset);

		grabarItem = new JMenuItem("Grabar numero");
		grabarItem.setMnemonic('g');
		grabarItem.addActionListener(this);
		leerItem = new JMenuItem("Leer numero");
		leerItem.setMnemonic('l');
		leerItem.addActionListener(this);
		archivoMenu = new JMenu("Archivo");
		archivoMenu.add(grabarItem);
		archivoMenu.add(leerItem);
		resetItem = new JMenuItem("Reset");
		resetItem.setMnemonic('r');
		resetItem.addActionListener(this);
		salirItem = new JMenuItem("Salir");
		salirItem.setMnemonic('s');
		salirItem.addActionListener(this);
		movilMenu = new JMenu("Movil");
		movilMenu.add(resetItem);
		movilMenu.addSeparator();
		movilMenu.add(salirItem);
		acercaDeItem = new JMenuItem("Acerca de ...");
		acercaDeItem.setMnemonic('c');
		acercaDeItem.addActionListener(this);
		otrosMenu = new JMenu("Otros");
		otrosMenu.add(acercaDeItem);
		menuPrincipal = new JMenuBar();
		menuPrincipal.add(archivoMenu);
		menuPrincipal.add(movilMenu);
		menuPrincipal.add(otrosMenu);
		this.setJMenuBar(menuPrincipal);

		this.setFocusable(true);
	
		this.addKeyListener(new EventosTeclado());
		this.setResizable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salidaPrograma();
			}
		});
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == leerItem) {
			String tmp = leerArchivo();
			if (tmp != null && !tmp.isEmpty()) {
				JOptionPane.showMessageDialog(this, tmp, "Numeros",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (e.getSource() == grabarItem) {
			try {
				if (!display.getText().isEmpty()) {
					grabarArchivo(display.getText());
				}else{
					JOptionPane.showMessageDialog(this, "El numero esta vacio");
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this,
						"Ha ocurrido un error escribiendo el archivo");
			}
		}
		if (e.getSource() == resetItem) {
			reset.doClick();
		}
		if (e.getSource() == salirItem) {
			salidaPrograma();
		}
		if (e.getSource() == acercaDeItem) {
			JOptionPane.showMessageDialog(this,
					"Telefono movil\n Xesus Gonzalez");
		}
	}

	public String leerArchivo() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			try {
				Scanner sc = new Scanner(f);
				String numeros = "";
				while (sc.hasNext()) {
					numeros += sc.nextLine() + "\n";
				}
				sc.close();
				return numeros;
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "El archivo no existe.");
			}
		}
		return null;
	}

	public void grabarArchivo(String numero) throws IOException {
		JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (!f.exists()) {
				f.createNewFile();
			}
			if (JOptionPane.showConfirmDialog(this,
					"Esta seguro de que " + "desea agregar el numero " + numero
							+ " al archivo" + f.getName() + "?",
					"Agregar numero", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				PrintWriter pw = new PrintWriter(new FileWriter(f, true));
				pw.println(numero);
				pw.close();
			}

		}
	}

	public void salidaPrograma() {
		if (JOptionPane.showConfirmDialog(this, "Desea salir del programa",
				"Salida", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			Ventana.this.dispose();
		}
	}

	public class EventosRaton extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			if (!((JButton) e.getSource()).getBackground().equals(Color.BLUE))
				((JButton) e.getSource()).setBackground(Color.YELLOW);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!((JButton) e.getSource()).getBackground().equals(Color.BLUE))
				((JButton) e.getSource()).setBackground(null);
		}
	}

	public class EventosTeclado extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
				teclado[Integer.parseInt("" + e.getKeyChar()) - 1].doClick();
			}
			switch (e.getKeyChar()) {
			case '*':
				teclado[9].doClick();
				break;
			case '0':
				teclado[10].doClick();
				break;
			case '#':
				teclado[11].doClick();
				break;
			}
		}
	}
}
