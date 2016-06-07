package com.spshop.stylistpark.collageviews;


public class EditList {
	
	public interface UndoRedoListener{
		public void undo(Edit edit);
		public void redo(Edit edit);
	}
	
	private int index = -1;
	
	SizedStack<Edit> editList;
	UndoRedoListener listener;

	public EditList(int maxSize, UndoRedoListener listener) {
		editList = new SizedStack<Edit>(maxSize);
		this.listener = listener;
	}
	
	public void addRecord(Edit edit){
		for(int i = index+1; i < editList.size();i++){
			editList.pop();
		}
		editList.push(edit);
		index = editList.size() - 1;
	}
	
	public void clear(){
	    editList.clear();
	}
	
	public void undo(){
		if(editList.isEmpty()) return;
		if(index < 0) return;
		Edit e = editList.get(index);
//		Log.d(TAG, "index = "+index+", type = "+e.type.name());
		listener.undo(e);
		index--;
	}
	
	public void redo(){
		if(editList.isEmpty()) return;
		if(index >= editList.size() - 1) return;
		index++;
		Edit e = editList.get(index);
//		Log.d(TAG, "index = "+index+", type = "+e.type.name());
		listener.redo(e);
	}
	
	public boolean isEmpty(){
		return editList.isEmpty();
	}

}
