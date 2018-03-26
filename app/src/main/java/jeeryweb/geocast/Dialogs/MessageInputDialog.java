package jeeryweb.geocast.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import jeeryweb.geocast.R;

/**
 * Created by Jeery on 18-03-2018.
 */

public class MessageInputDialog extends DialogFragment {

    FloatingActionMenu floatingActionMenu;


    public void passFloatMenu(FloatingActionMenu floatingActionMenu)
    {
        this.floatingActionMenu= floatingActionMenu;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.custom_message_input_dialog, null))
                // Add action buttons
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get the message and send to server

                        floatingActionMenu.close(true);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MessageInputDialog.this.getDialog().cancel();

                    }
                });
        builder.setCancelable(false);
        Dialog dialog= builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;

    }



}
