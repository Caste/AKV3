package vamixUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Main GUI of Vamix program.
 * (A singleton class.)
 * 
 * @author acas212
 */
@SuppressWarnings("serial")
public class VamixGUI extends JFrame implements ActionListener, ChangeListener {

	private static VamixGUI _guiInstance = null;

	private JTabbedPane _tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel _fileTab = new DirPane(new File(System.getProperty("user.dir")));
	private JPanel _audioTab = new JPanel();
	private JPanel _textTab = new JPanel();

	private JPanel _leftPanel = new JPanel();
	
	private JPanel _topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JButton _saveButton = new JButton("Save");
	private JButton _fileButton = new FileChooseButton("Open File");
	private JButton _downloadButton = new JButton("Download");

	private JPanel _botButtonPanel = new JPanel();
	
	private JPanel _mediaSliderPanel = new JPanel(new BorderLayout(10, 10));
	private SeekBar _seekBar = new SeekBar(0);
	private JLabel _timeLabel = new JLabel();
	
	private JPanel _mediaControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	
	private JPanel _mediaButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JToggleButton _playButton = new JToggleButton();
	private JButton _rewindButton = new JButton();
	private JButton _fastFwdButton = new JButton();
	private JButton _stopButton = new JButton();
	
	private JPanel _volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JSlider  _volumeControl = new JSlider(0, 150);
	private JToggleButton _muteButton = new JToggleButton();
	
	private static VLCPlayerPane _playerPanel = VLCPlayerPane.getInstance(); 

	private boolean _isMediaLoaded = false;
	private static boolean _isPlayIcon = true;

	/**
	 * Private constructor for VamixGUI.
	 */
	private VamixGUI() {
		super("VAMIX");
		setSize(935, 625);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Disable resizing.
		setResizable(false);

		//Set the Layout for the main GUI.
		setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
		
		//Set size and Layout of leftPanel.
		_leftPanel.setPreferredSize(new Dimension(600, 550));
		_leftPanel.setLayout(new BorderLayout(10,10));
		
		//Set size and add tabs to tabbedPane.
		_tabbedPane.setPreferredSize(new Dimension(300, 550));
		_tabbedPane.add(_fileTab, "Files");
		_tabbedPane.add(_audioTab, "Audio");
		_tabbedPane.add(_textTab, "Text");
		
		//Add leftPanel and TabbedPane to the Main GUI.
		add(_leftPanel, BorderLayout.CENTER);
		add(_tabbedPane, BorderLayout.EAST);
		
		//Set sizes of the top button panel and its buttons.
		_topButtonPanel.setPreferredSize(new Dimension(600,50));
		_fileButton.setPreferredSize(new Dimension(170,50));
		_saveButton.setPreferredSize(new Dimension(170,50));
		_downloadButton.setPreferredSize(new Dimension(170,50));

		//Set icons onto top buttons.
		_fileButton.setIcon(new ImageIcon(new ResImage("folder.png").getResImage()));
		_saveButton.setIcon(new ImageIcon(new ResImage("floppy.png").getResImage().getScaledInstance(40, 40, 0)));
		_downloadButton.setIcon(new ImageIcon(new ResImage("download.png").getResImage()));

		//Add top buttons to the top panel.
		_topButtonPanel.add(_fileButton);
		_topButtonPanel.add(_saveButton);
		_topButtonPanel.add(_downloadButton);

		//Set size of the VLC player panel.
		_playerPanel.setPreferredSize(new Dimension(600,400));
		
		//Set event handler to reset play button when end of video / audio is reached.
		_playerPanel.addMediaEventHandler(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				_playButton.setSelected(false); //Now reset to play button.
				_isMediaLoaded = false;
				_isPlayIcon = true;
				_playerPanel.stop();
			}
		});

		//Set size and layout of the bottom button panel.
		_botButtonPanel.setLayout(new BorderLayout(10,10));
		_botButtonPanel.setPreferredSize(new Dimension(600,70));
		_botButtonPanel.add(_mediaSliderPanel,BorderLayout.NORTH);
		_botButtonPanel.add(_mediaControlPanel,BorderLayout.CENTER);

		//Set size and add the seek bar to the panel.
		_mediaSliderPanel.setPreferredSize(new Dimension(600,20));
		_mediaSliderPanel.add(_seekBar, BorderLayout.CENTER);
		
		//Add time to the slider panel.
		_mediaSliderPanel.add(_timeLabel, BorderLayout.WEST);
		
		//Set size and layout of the media control panel.
		//(Contains button panel and volume control.)
		_mediaControlPanel.setLayout(new GridLayout(0,2));
		_mediaControlPanel.setPreferredSize(new Dimension(600,50));

		//Set layout of panel containing all media control buttons:
		_mediaButtonPanel.setLayout(new GridLayout(0,4));

		//Place appropriate icons on the media control buttons.
		_playButton.setIcon(new ImageIcon(new ResImage("play.png").getResImage()));
		_playButton.setSelectedIcon(new ImageIcon(new ResImage("pause.png").getResImage()));

		_rewindButton.setIcon(new ImageIcon(new ResImage("rewind.png").getResImage()));
		_stopButton.setIcon(new ImageIcon(new ResImage("stop.png").getResImage()));
		_fastFwdButton.setIcon(new ImageIcon(new ResImage("fastForward.png").getResImage()));

		//Add buttons to button panel...
		_mediaButtonPanel.add(_playButton);
		_mediaButtonPanel.add(_rewindButton);
		_mediaButtonPanel.add(_stopButton);
		_mediaButtonPanel.add(_fastFwdButton);
		
		//...then add button panel to final control panel.
		_mediaControlPanel.add(_mediaButtonPanel);

		//Set layout of volume panel.
		_volumePanel.setLayout(new BorderLayout(0,0));

		//Set appropriate icons to the mute button.
		_muteButton.setIcon(new ImageIcon(new ResImage("volume.png").getResImage()));
		_muteButton.setSelectedIcon(new ImageIcon(new ResImage("noVolume.png").getResImage()));

		//Add mute and volume slider to volume panel...
		_volumePanel.add(_volumeControl, BorderLayout.CENTER);
		_volumePanel.add(_muteButton, BorderLayout.WEST);

		//...then add volume panel to final control panel.
		_mediaControlPanel.add(_volumePanel);

		//Add top, bottom and the VLC Player panels to the left panel.
		_leftPanel.add(_topButtonPanel, BorderLayout.NORTH);
		_leftPanel.add(_playerPanel, BorderLayout.CENTER);
		_leftPanel.add(_botButtonPanel, BorderLayout.SOUTH);

		//Add Main frame as listener to all appropriate components.
		_saveButton.addActionListener(this);
		_downloadButton.addActionListener(this);
		
		_playButton.addActionListener(this);
		_stopButton.addActionListener(this);
		_fastFwdButton.addActionListener(this);
		_rewindButton.addActionListener(this);
		
		_muteButton.addActionListener(this);
		_volumeControl.addChangeListener(this);
		
		//Set timer component to continuously update the time label:
		Timer currentTimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Get current time of media from media player (in milliseconds).
				Long mediaTime = _playerPanel.getTime();
				
				//Convert time to string.
				String currentTime = String.format("%02d:%02d:%02d", 
						TimeUnit.MILLISECONDS.toHours(mediaTime),
						TimeUnit.MILLISECONDS.toMinutes(mediaTime) - 
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mediaTime)),
						TimeUnit.MILLISECONDS.toSeconds(mediaTime) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaTime))
						);
				
				_timeLabel.setText(currentTime);
				
				_seekBar.setValue(mediaTime.intValue());
			}
		});
		//Start 'time updater' timer.
		currentTimer.start();
		
		//Add listener to fast forward button.
		_fastFwdButton.addMouseListener(new MouseAdapter() {
			//Timer to continually skip forward.
			private Timer timePressed = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					_playerPanel.skipForward();
				}
			});

			//Start timer to continually skip when mouse is pressed.
			@Override
			public void mousePressed(MouseEvent me) {
				timePressed.start();
			}

			//Stop timer when mouse is released.
			@Override
			public void mouseReleased(MouseEvent me) {
				timePressed.stop();
			}
		});

		//Add listener to rewind button.
		_rewindButton.addMouseListener(new MouseAdapter() {
			//Timer to continually skip backward.
			private Timer timePressed = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					_playerPanel.skipBackward();
				}
			});

			//Start timer to continually skip when mouse is pressed.
			@Override
			public void mousePressed(MouseEvent me) {
				timePressed.start();
			}

			//Stop timer when mouse is released.
			@Override
			public void mouseReleased(MouseEvent me) {
				timePressed.stop();
			}
		});

		//Finally set frame as visible.
		this.setVisible(true);
	}

	/**
	 * Method to get single instance of VamixGUI.
	 */
	public static VamixGUI getInstance() {
		if (_guiInstance == null) {
			_guiInstance = new VamixGUI();
		}
		return _guiInstance;
	}

	/**
	 * Method explaining actions performed depending on
	 * which button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		//If play button pressed:
		if (ae.getSource() == _playButton) {
			//If no media has started being played...
			if (!_isMediaLoaded) {		
					//...play media specified by mediaPath.
					_playerPanel.play();
					setPlay();
					
			} else { 
				//Else toggle pause on the media player.
				_playerPanel.pause();
			}

		//If stop button pressed:
		} else if (ae.getSource() == _stopButton) {
			//Stop media, while reassigning the appropriate booleans
			//and resetting the play button.
			_playerPanel.stop();
			_playButton.setSelected(false);
			_isMediaLoaded = false;
			
		//If mute button pressed:	
		} else if (ae.getSource() == _muteButton) {
			//Toggle mute on media player.
			_playerPanel.mute();
			
		//If download button pressed:
		} else if (ae.getSource() == _downloadButton) {
			//Bring up new dialog window which handles downloads.
			new DownloadHandler(VamixGUI.this);
		} else if (ae.getSource() == _saveButton) {
			//TODO testing for now:
			//_playerPanel.parseMedia();
			System.out.println(_playerPanel.getLength());
		}
	}

	/**
	 * Method for when volume slider changes.
	 */
	@Override
	public void stateChanged(ChangeEvent ce) {
		//If volume slider is changed:
		if (ce.getSource() == _volumeControl) {
			//Set new volume for media player.
			_playerPanel.setVolume(_volumeControl.getValue());
		}
	}

	/**
	 * Method for setting the play button as selected
	 * and changing the appropriate booleans.
	 */
	public void setPlay() {
		_playButton.setSelected(true);
		_isMediaLoaded = true;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		_seekBar.setNewTotalLength(_playerPanel.getLength());
		System.out.println(_playerPanel.getLength());
	}

	/**
	 * Main method to start running the GUI.
	 * @param args
	 */
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				VamixGUI.getInstance();
			}
		});
	}
}


