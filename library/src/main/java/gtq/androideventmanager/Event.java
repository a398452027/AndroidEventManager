package gtq.androideventmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Event {
	
	protected final int 	mEventCode;
	
	protected boolean		mIsSuccess = false;
	
	protected Exception		mFailException;
	
	protected Object 		mParams[];
	
	protected int			mHashCode;
	
	protected List<Object>	mReturnParams;
	
	public Event(int eventCode,Object params[]){
		mEventCode = eventCode;
		mParams = params;
		mHashCode = getEventCode();
		if(mParams != null){
			for(Object obj : mParams){
				if(obj != null){
					mHashCode = mHashCode * 29 + obj.hashCode();
				}
			}
		}
	}

	public void setmParams(Object[] mParams) {
		this.mParams = mParams;
	}

	public int 		getEventCode(){
		return mEventCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true;
		}
		if(o != null && o instanceof Event){
			final Event other = (Event)o;
			if(getEventCode() == other.getEventCode()){
				return Arrays.equals(mParams, other.getParams());
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mHashCode;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("code=");
		sb.append(mEventCode);
		sb.append("{");
		for(Object obj : mParams){
			if(obj != null){
				sb.append(obj.toString()).append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public void			setSuccess(boolean bSuccess){
		mIsSuccess = bSuccess;
	}
	
	public boolean		isSuccess(){
		return mIsSuccess;
	}
	
	public Object[]		getParams(){
		return mParams;
	}
	
	public Object		getParamAtIndex(int index){
		if(mParams != null && mParams.length > index){
			return mParams[index];
		}
		return null;
	}
	
	public void			setFailException(Exception e){
		mFailException = e;
	}
	
	public void			setResult(Event other){
		if(getEventCode() == other.getEventCode()){
			mReturnParams = other.mReturnParams;
			setSuccess(other.isSuccess());
			setFailException(other.getFailException());
		}
	}
	
	public String		getFailMessage(){
		return mFailException == null ? null : mFailException.getMessage();
	}
	
	public Exception	getFailException(){
		return mFailException;
	}
	
	public boolean		isFailByNet(){
		return getFailMessage() == null;
	}
	
	public void			addReturnParam(Object obj){
		if(mReturnParams == null){
			mReturnParams = new ArrayList<Object>();
		}
		mReturnParams.add(obj);
	}
	
	public Object		getReturnParamAtIndex(int index){
		if(mReturnParams == null || index >= mReturnParams.size()){
			return null;
		}
		return mReturnParams.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T		findReturnParam(Class<T> c){
		if(mReturnParams != null){
			for(Object obj : mReturnParams){
				if(c.isInstance(obj)){
					return (T)obj;
				}
			}
		}
		return null;
	}
}
