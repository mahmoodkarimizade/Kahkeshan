package ir.galaxycell.kahkeshan.InterFace;

/**
 * Created by Admin on 11/09/2017.
 */
public class mahmood {

    private static mahmood mInstance;
    private SeeEditProductImageProfileClickListener mListener;
    private boolean mState;
    public interface SeeEditProductImageProfileClickListener
    {
        public void OnImageProfileClickListener();
    }

    public static mahmood getInstance() {
        if(mInstance == null) {
            mInstance = new mahmood();
        }
        return mInstance;
    }

    public void setListener(SeeEditProductImageProfileClickListener listener) {
        mListener = listener;
    }

    public void changeState(boolean state) {
        if(mListener != null) {
            mState = state;
            notifyStateChange();
        }
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.OnImageProfileClickListener();
    }
}
