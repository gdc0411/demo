package com.lecloud.skin.ui;

import android.os.Bundle;


public interface LetvUIListener {
    
    void onUIEvent(int event, Bundle bundle);
    
	void onClickPlay();
	
//	void onClickDownload();
	
	void onStartSeek();
	
	void onSeekTo(float per);
	
	void onSwitchPanoVideoMode(int mode);
	
    void onSetDefination(int type);

    void resetPlay();
    
    void setRequestedOrientation(int requestedOrientation);
    
    void onProgressChanged(int progress);
    
}
