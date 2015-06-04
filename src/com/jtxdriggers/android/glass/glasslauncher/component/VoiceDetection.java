package com.jtxdriggers.android.glass.glasslauncher.component;

import android.content.Context;
import android.util.Log;

import com.google.glass.voice.VoiceCommand;
import com.google.glass.voice.VoiceConfig;
import com.google.glass.voice.VoiceInputHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ramon on 22.04.2014.
 */
public class VoiceDetection extends StubVoiceListener {

	private static final String THIS = VoiceDetection.class.getSimpleName();

	private final String[] phrases;
	private final String hotword;
	private final boolean[] enabled;
	private final VoiceConfig voiceConfig;
	private final boolean hotwordOnlyMode;
	private boolean hotwordOnly;
	private VoiceInputHelper mVoiceInputHelper;
	private VoiceDetectionListener mListener;
	private boolean mRunning = true;

	private static VoiceConfig activeVoiceConfig;

	public VoiceDetection(Context context, String hotword, VoiceDetectionListener listener, boolean alwaysListen, String... phrases) {
		mVoiceInputHelper = new VoiceInputHelper(context, this);

		this.hotwordOnlyMode = !alwaysListen;
		hotwordOnly = this.hotwordOnlyMode;

		this.hotword = hotword;
		this.phrases = phrases;

		enabled = new boolean[phrases.length];
        Arrays.fill(enabled, Boolean.TRUE);

		String[] allPhrases = new String[phrases.length + 1];
		System.arraycopy(phrases, 0, allPhrases, 1, phrases.length);
		allPhrases[0] = hotword;

		voiceConfig = new VoiceConfig(allPhrases);
		voiceConfig.setShouldSaveAudio(false);

		mListener = listener;
	}

	public boolean isEnabled(int phraseID) {
		return enabled[phraseID];
	}

	public void setEnabled(int phraseID, boolean enabled) {
		this.enabled[phraseID] = enabled;
	}

	/**
	 * Commit changes to the enabled phrases
	 *
	 * Current implementation, always leaves all phrases active, only hides them, so update of VoiceConfig is never necessary
	 * (This is included to be compatible to implementations that actually change VoiceConfig)
	 *
	 * @return false if no changes were made
	 */
	public boolean update() {
		return false;
	}

	/**
	 * If the VoiceService is ready, refresh our Config
	 *
	@Override
	public void onVoiceServiceConnected() {
		super.onVoiceServiceConnected();
		mVoiceInputHelper.setVoiceConfig(mVoiceConfig);
	}*/

    public void setListener(VoiceDetectionListener mListener){
        this.mListener = mListener;
    }

	@Override
	public VoiceConfig onVoiceCommand(VoiceCommand vc) {
		String literal = vc.getLiteral();

//		if (mListener == null) {
//			mVoiceInputHelper.removeVoiceServiceListener();
//			return null;
//		}
        Log.d("glass", "voive detected:"+literal);
		if (literal.equalsIgnoreCase(hotword)) { // Hotword
			Log.i(THIS, "Hotword detected");
			mListener.onHotwordDetected();

			if (hotwordOnlyMode) {
				hotwordOnly = false;
			}

			return null;
		}

		if (!hotwordOnly) {
			for (int i = 0; i < phrases.length; ++i) {
				String item = phrases[i];
				if (item.equalsIgnoreCase(literal) && enabled[i]) {// XE21 definitively converts the first letter to upper case!
					Log.i(THIS, String.format("command %s", item));
					mListener.onPhraseDetected(i, item);

					//if (hotwordOnlyMode)
					//	hotwordOnly = true;

					return null;
				}
			}
		}

		return null;
	}

	public void start(VoiceDetectionListener mListener) {
		mRunning = true;
		Log.d(THIS, "Starting Voice Recognition for "+ this);
        mVoiceInputHelper.setVoiceConfig(voiceConfig);
		activeVoiceConfig = voiceConfig;
        this.mListener = mListener;
	}

	public void stop() {
		mRunning = false;
		if (activeVoiceConfig == voiceConfig) {
			mVoiceInputHelper.setVoiceConfig(null);//This stops the service (which is a singleton), Android starts a new activity before it stops the old one
			//-> we started the new recognition and then stopped it immediately
			Log.d(THIS, "Stopping Voice Recognition for "+ this);
		}
	}

	@Override
	public boolean isRunning() {
		return mRunning;
	}

	public String[] getEnabledPhrases() {
		List<String> phr = new ArrayList<String>();
		for (int i=0; i<phrases.length; ++i) {
			if (enabled[i]) {
				phr.add(phrases[i]);
			}
		}

		return phr.toArray(new String[phr.size()]);
	}

	public String getHotword() {
		return hotword;
	}

	public int[] getEnabledIds() {
		List<Integer> ids = new ArrayList<Integer>();
		for (int i=0; i<enabled.length; ++i) {
			if (enabled[i])
				ids.add(i);
		}
		int[] idArr = new int[ids.size()];
		int j = 0;
		for (int i: ids) {
			idArr[j++] = i;
		}

		return idArr;
	}

	public interface VoiceDetectionListener {
		public void onHotwordDetected();
		public void onPhraseDetected(int index, String phrase);
	}
}
