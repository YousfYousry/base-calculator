package com.example.basecalculator;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

class StyleCallback implements ActionMode.Callback {
    private EditText bodyView;
    private notes anouncement;
    private boolean styleOpened = false;
    private ActionMode Mode;

    void setStyleOpened(boolean styleOpened) {
        this.styleOpened = styleOpened;
    }

    void setAnouncement(notes anouncement) {
        this.anouncement = anouncement;
    }

    void setBodyView(EditText bodyView) {
        this.bodyView = bodyView;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (!styleOpened) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.style, menu);
            menu.removeItem(android.R.id.selectAll);
            Mode = mode;
        }
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Mode = mode;
        return false;
    }

    void closeAction() {
        int start = bodyView.getSelectionStart(), end = bodyView.getSelectionEnd();
        if (Mode != null) {
            Mode.finish();
        }
        bodyView.setSelection(start, end);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Mode = mode;
        if (item.getItemId() == R.id.Format) {
            anouncement.showView();
            closeAction();
            return true;
        }
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
        Mode = mode;
    }
}