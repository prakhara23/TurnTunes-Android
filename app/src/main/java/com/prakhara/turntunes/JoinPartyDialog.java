package com.prakhara.turntunes;

//import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinPartyDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mainView = inflater.inflate(R.layout.join_dialog, null);

        builder.setView(mainView)
                .setTitle(R.string.joinBut)
                .setPositiveButton(R.string.joinDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //checkInput(dialog);
                        Dialog dialogView = (Dialog) dialog;
                        EditText partyText = (EditText) dialogView.findViewById(R.id.partyName);
                        String partyName = partyText.getText().toString().trim();
                        ((MainActivity) getActivity()).joinParty(partyName);
                    }
                })
                .setNegativeButton(R.string.cancelDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        JoinPartyDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
