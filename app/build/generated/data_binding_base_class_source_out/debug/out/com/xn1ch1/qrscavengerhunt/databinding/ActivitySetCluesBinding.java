// Generated by view binder compiler. Do not edit!
package com.xn1ch1.qrscavengerhunt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.xn1ch1.qrscavengerhunt.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySetCluesBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout clueInputOverlay;

  @NonNull
  public final LinearLayout clueTable;

  @NonNull
  public final ImageView setCluesBackButton;

  @NonNull
  public final ImageView setCluesFinishButton;

  @NonNull
  public final TextView setCluesInputCancel;

  @NonNull
  public final TextView setCluesInputClear;

  @NonNull
  public final TextView setCluesInputDone;

  @NonNull
  public final ImageView setCluesResetButton;

  @NonNull
  public final EditText setCluesTextInput;

  @NonNull
  public final TextView setCluesTitle;

  private ActivitySetCluesBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout clueInputOverlay, @NonNull LinearLayout clueTable,
      @NonNull ImageView setCluesBackButton, @NonNull ImageView setCluesFinishButton,
      @NonNull TextView setCluesInputCancel, @NonNull TextView setCluesInputClear,
      @NonNull TextView setCluesInputDone, @NonNull ImageView setCluesResetButton,
      @NonNull EditText setCluesTextInput, @NonNull TextView setCluesTitle) {
    this.rootView = rootView;
    this.clueInputOverlay = clueInputOverlay;
    this.clueTable = clueTable;
    this.setCluesBackButton = setCluesBackButton;
    this.setCluesFinishButton = setCluesFinishButton;
    this.setCluesInputCancel = setCluesInputCancel;
    this.setCluesInputClear = setCluesInputClear;
    this.setCluesInputDone = setCluesInputDone;
    this.setCluesResetButton = setCluesResetButton;
    this.setCluesTextInput = setCluesTextInput;
    this.setCluesTitle = setCluesTitle;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySetCluesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySetCluesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_set_clues, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySetCluesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.clueInputOverlay;
      ConstraintLayout clueInputOverlay = rootView.findViewById(id);
      if (clueInputOverlay == null) {
        break missingId;
      }

      id = R.id.clueTable;
      LinearLayout clueTable = rootView.findViewById(id);
      if (clueTable == null) {
        break missingId;
      }

      id = R.id.setCluesBackButton;
      ImageView setCluesBackButton = rootView.findViewById(id);
      if (setCluesBackButton == null) {
        break missingId;
      }

      id = R.id.setCluesFinishButton;
      ImageView setCluesFinishButton = rootView.findViewById(id);
      if (setCluesFinishButton == null) {
        break missingId;
      }

      id = R.id.setCluesInputCancel;
      TextView setCluesInputCancel = rootView.findViewById(id);
      if (setCluesInputCancel == null) {
        break missingId;
      }

      id = R.id.setCluesInputClear;
      TextView setCluesInputClear = rootView.findViewById(id);
      if (setCluesInputClear == null) {
        break missingId;
      }

      id = R.id.setCluesInputDone;
      TextView setCluesInputDone = rootView.findViewById(id);
      if (setCluesInputDone == null) {
        break missingId;
      }

      id = R.id.setCluesResetButton;
      ImageView setCluesResetButton = rootView.findViewById(id);
      if (setCluesResetButton == null) {
        break missingId;
      }

      id = R.id.setCluesTextInput;
      EditText setCluesTextInput = rootView.findViewById(id);
      if (setCluesTextInput == null) {
        break missingId;
      }

      id = R.id.setCluesTitle;
      TextView setCluesTitle = rootView.findViewById(id);
      if (setCluesTitle == null) {
        break missingId;
      }

      return new ActivitySetCluesBinding((ConstraintLayout) rootView, clueInputOverlay, clueTable,
          setCluesBackButton, setCluesFinishButton, setCluesInputCancel, setCluesInputClear,
          setCluesInputDone, setCluesResetButton, setCluesTextInput, setCluesTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}