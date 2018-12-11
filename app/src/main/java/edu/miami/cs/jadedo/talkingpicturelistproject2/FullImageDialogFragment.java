package edu.miami.cs.jadedo.talkingpicturelistproject2;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;
import android.app.Activity;
import android.content.DialogInterface;

public class FullImageDialogFragment extends DialogFragment implements DialogInterface.OnDismissListener{
    View dialogView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView fullImage;

        dialogView = inflater.inflate(R.layout.dialog, container);

        fullImage = (ImageView) dialogView.findViewById(R.id.full_image);
        fullImage.setImageURI(Uri.parse(getTag()));

        ((Button) dialogView.findViewById(R.id.btn_dismiss)).setOnClickListener(myClickHandler);


        return (dialogView);
    }

    private View.OnClickListener myClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            switch (view.getId()) {
                case R.id.btn_dismiss:
                    ((MyTalkingPictureList)getActivity()).stopTalkingAndPlayMusic();
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    public interface DialogOver{

        public void stopTalkingAndPlayMusic();
    }

}
