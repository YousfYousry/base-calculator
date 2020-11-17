package com.example.basecalculator;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

import static com.example.basecalculator.RegisterActivity.SHARED_PREFS;

public class notes extends AppCompatActivity {
    FloatingActionButton floating;
    ArrayList<note> announce = new ArrayList<>();
    ImageView textColor, backgroundColor;
    ImageButton bold, italic, underLine, textC, textB;
    int posToEdit = 0;
    long temp, max;
    EditTextC announcementE;
    ListView announcementList;
    ScrollView scrollViewE;
    LinearLayout style, textColorlist, backgroundColorlist;
    boolean editMode = false;
    StyleCallback styleCallback = new StyleCallback();
    notes anouncement = this;
    String ColorString = "#000000", bColorString = "#FFFFFF";
    boolean styleOpened = false, isbold = false, isItalic = false, isUnder = false, removeSelection = false, textCOpened = false, textBOpened = false, selected = true, editpost = false;
    Context context = this;


    public void deletePost(int pos) {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("notes").child(Long.toString(announce.get(pos).getId())).removeValue();
        Toast.makeText(this, "Post is deleted", Toast.LENGTH_SHORT).show();
    }

    public void ecitPost(int pos) {
        editpost = true;
        posToEdit = pos;
        EditPost(pos);
    }

    public void bold(View view) {
        if (isbold) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveStyle(spannable, startSelection, endSelection, Typeface.BOLD);
            isbold = false;
            bold.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new StyleSpan(Typeface.BOLD);
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            isbold = true;
            selected = true;
            bold.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void italic(View view) {
        if (isItalic) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveStyle(spannable, startSelection, endSelection, Typeface.ITALIC);
            isItalic = false;
            italic.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new StyleSpan(Typeface.ITALIC);
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            selected = true;
            isItalic = true;
            italic.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void underLine(View view) {
        if (isUnder) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveOne(spannable, startSelection, endSelection, UnderlineSpan.class);
            isUnder = false;
            underLine.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new UnderlineSpan();
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            selected = true;
            isUnder = true;
            underLine.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void textColor(View view) {
        if (textBOpened) {
            slideViewWidth(backgroundColorlist, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }

        if (!textCOpened) {
            slideViewWidth(textColorlist, 0, dpToPx(310), 300);
            textCOpened = true;
            textC.setBackgroundResource(R.drawable.ripple_grey);
        } else {
            slideViewWidth(textColorlist, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }

    public void black(View view) {
        colorChoosed("#000000");
    }

    public void red(View view) {
        colorChoosed("#f44235");
    }

    public void blue(View view) {
        colorChoosed("#2296f3");
    }

    public void green(View view) {
        colorChoosed("#4caf50");
    }

    public void yellow(View view) {
        colorChoosed("#ffc100");
    }

    public void grey(View view) {
        colorChoosed("#9e9e9e");
    }

    public void colorChoosed(String colorString) {
        selected = false;
        CharacterStyle cs;
        ColorString = colorString;
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, ForegroundColorSpan.class);

        cs = new ForegroundColorSpan(Color.parseColor(colorString));
        ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        textColor.setBackgroundColor(Color.parseColor(colorString));
        closeColors();
        selected = true;
    }

    public void closeColors() {
        if (textBOpened) {
            slideViewWidth(backgroundColorlist, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
        if (textCOpened) {
            slideViewWidth(textColorlist, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }

    public void textBack(View view) {
        if (textCOpened) {
            slideViewWidth(textColorlist, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }

        if (!textBOpened) {
            slideViewWidth(backgroundColorlist, 0, dpToPx(310), 300);
            textBOpened = true;
            textB.setBackgroundResource(R.drawable.ripple_grey);
        } else {
            slideViewWidth(backgroundColorlist, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }


    public void blackB(View view) {
        selected = false;
        bColorString = "#FFFFFF";
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, BackgroundColorSpan.class);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        backgroundColor.setBackgroundColor(Color.parseColor(bColorString));
        closeColors();
        selected = true;
    }

    public void redB(View view) {
        bColorChoosed("#ef9a9a");
    }

    public void blueB(View view) {
        bColorChoosed("#90caf9");
    }

    public void greenB(View view) {
        bColorChoosed("#a5d6a7");
    }

    public void yellowB(View view) {
        bColorChoosed("#ffe082");
    }

    public void greyB(View view) {
        bColorChoosed("#e0e0e0");
    }

    public void bColorChoosed(String colorString) {
        selected = false;
        CharacterStyle cs;
        bColorString = colorString;
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, BackgroundColorSpan.class);

        cs = new BackgroundColorSpan(Color.parseColor(colorString));
        ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        backgroundColor.setBackgroundColor(Color.parseColor(colorString));
        closeColors();
        selected = true;
    }

    public void removeAll(View view) {
        int startSelection = announcementE.getSelectionStart();
        int endSelection = announcementE.getSelectionEnd();
        Spannable spannable = announcementE.getText();
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveAll(spannable, startSelection, endSelection);
        removeSelection = true;
        ColorString = "#000000";
        bColorString = "#FFFFFF";
        setButtons(startSelection, endSelection);
    }

    public void close(View view) {
        closeStyle();
    }

    public void closeStyle() {
        if (styleOpened) {
            slideViewHeight(style, dpToPx(54), 0, 300);
            styleOpened = false;
            styleCallback.setStyleOpened(false);
            styleCallback.closeAction();
        }
    }

    public void paint(View view) {
        showView();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void cancel(View view) {
        editMode = false;
        editpost = false;
        closeStyle();
        closeColors();
        hideKeyBoard(announcementE);
        floating.setImageResource(R.drawable.ic_add_black_24dp);
        announcementList.setVisibility(View.VISIBLE);
        scrollViewE.setVisibility(View.GONE);
    }

    public void Edit(View view) {
        if (!editMode) {
            editMode = true;

            slideViewHeight(style, 0, dpToPx(54), 300);
            styleOpened = true;
            styleCallback.setStyleOpened(true);

            announcementList.setVisibility(View.GONE);
            scrollViewE.setVisibility(View.VISIBLE);
            floating.setImageResource(R.drawable.ic_save_black_24dp);
            selected = false;
            showSoftKeyboard(announcementE);
            announcementE.setText("");
            selected = true;
        } else {
            hideKeyBoard(announcementE);
            closeStyle();
            closeColors();
            floating.setImageResource(R.drawable.ic_add_black_24dp);
            if (editpost) {

                DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("notes").child(Long.toString(announce.get(posToEdit).getId())).setValue(Html.toHtml(announcementE.getText()));
                        Toast.makeText(context, "Post is saved successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            } else {
                temp = -1;
                DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        max = 0;
                        for (DataSnapshot ds : dataSnapshot.child("notes").getChildren()) {
                            if (ds.getKey() != null) {
                                if (Long.parseLong(ds.getKey()) > max) {
                                    max = Long.parseLong(ds.getKey());
                                }
                            }
                        }
                        for (long i = 0; i <= max; i++) {
                            if (dataSnapshot.child("notes").child(Long.toString(i)).getValue() == null) {
                                temp = i;
                                break;
                            }
                        }
                        saveAnn();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            editMode = false;
            editpost = false;
            announcementList.setVisibility(View.VISIBLE);
            scrollViewE.setVisibility(View.GONE);
        }
    }

    public void saveAnn() {
        if (temp != -1) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("notes").child(Long.toString(temp)).setValue(Html.toHtml(announcementE.getText()));
        } else {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("notes").child(Long.toString(max + 1)).setValue(Html.toHtml(announcementE.getText()));
        }
        Toast.makeText(this, "Post is saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void EditPost(int pos) {
        if (!editMode) {
            showSoftKeyboard(announcementE);
            editMode = true;
            announcementList.setVisibility(View.GONE);
            scrollViewE.setVisibility(View.VISIBLE);


            slideViewHeight(style, 0, dpToPx(54), 300);
            styleOpened = true;
            styleCallback.setStyleOpened(true);

            selected = false;
            floating.setImageResource(R.drawable.ic_save_black_24dp);
            announcementE.setText(announce.get(pos).getAnnounce());
            selected = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        TypedValue tv = new TypedValue();
        FrameLayout frameLayout = findViewById(R.id.dummy);
        LinearLayout toolbar = findViewById(R.id.select);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            params.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + dpToPx(4);
            param2.height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        frameLayout.setLayoutParams(param2);
        toolbar.setLayoutParams(params);

        floating = findViewById(R.id.edit);
        floating.setImageResource(R.drawable.ic_add_black_24dp);
        textColorlist = findViewById(R.id.textColorlist);
        backgroundColorlist = findViewById(R.id.backgroundColorlist);
        announcementList = findViewById(R.id.announcementList);
        textC = findViewById(R.id.textC);
        textB = findViewById(R.id.textB);
        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underLine = findViewById(R.id.underLine);
        textColor = findViewById(R.id.textColor);
        backgroundColor = findViewById(R.id.backgroundColor);
        announcementE = findViewById(R.id.announceTextE);
        style = findViewById(R.id.style);
        styleCallback.setBodyView(announcementE);
        styleCallback.setAnouncement(anouncement);
        announcementE.setCustomSelectionActionModeCallback(styleCallback);
        announcementE.setAnouncement(anouncement);
        scrollViewE = findViewById(R.id.editMode);
        announcementE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTextSpan(start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary) & 0x00ffffff);

        announcementE.setHighlightColor(Color.parseColor("#782F5D87"));
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (announce != null) {
                    announce.clear();
                }
                max = 0;
                for (DataSnapshot ds : dataSnapshot.child("notes").getChildren()) {
                    if (ds.getKey() != null) {
                        if (Long.parseLong(ds.getKey()) > max) {
                            max = Long.parseLong(ds.getKey());
                        }
                    }
                }

                for (long i = 0; i <= max; i++) {
                    if (dataSnapshot.child("notes").child(Long.toString(i)).getValue() != null) {
                        announce.add(new note(Html.fromHtml(dataSnapshot.child("notes").child(Long.toString(i)).getValue().toString()), i));
                    }
                }
                noteAdapter arrayAdapter = new noteAdapter(context, R.layout.announce, announce);
                arrayAdapter.setAnouncement(anouncement);
                announcementList.setAdapter(arrayAdapter);
//                Toast.makeText(context,"Announcement is edited",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setTextSpan(int start, int lengthBefore, int lengthAfter) {
        if (isbold && selected) {
            announcementE.getText().setSpan(new StyleSpan(Typeface.BOLD), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isItalic && selected) {
            announcementE.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isUnder && selected) {
            announcementE.getText().setSpan(new UnderlineSpan(), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!isUnder && selected) {
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveOne(spannable, start, start + lengthAfter, UnderlineSpan.class);
        }
        if (selected) {
            announcementE.getText().setSpan(new ForegroundColorSpan(Color.parseColor(ColorString)), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            if (bColorString.compareTo("#FFFFFF") != 0) {
                announcementE.getText().setSpan(new BackgroundColorSpan(Color.parseColor(bColorString)), start, start + lengthAfter, Spanned
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void setButtons(int start, int end) {
        if (styleOpened) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
            if ((end - start) != 0 || removeSelection) {
                if (removeSelection) {
                    textColor.setBackgroundColor(Color.parseColor(ColorString));
                    backgroundColor.setBackgroundColor(Color.parseColor(bColorString));
                }
                if (!isBold(ssb, start, end) || removeSelection) {
                    isbold = false;
                    bold.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isbold = true;
                    bold.setBackgroundResource(R.drawable.ripple_grey);
                }

                if (!isItalic(ssb, start, end) || removeSelection) {
                    isItalic = false;
                    italic.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isItalic = true;
                    italic.setBackgroundResource(R.drawable.ripple_grey);
                }

                if (!isUnder(ssb, start, end) || removeSelection) {
                    isUnder = false;
                    underLine.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isUnder = true;
                    underLine.setBackgroundResource(R.drawable.ripple_grey);
                }
//                getColor(ssb, start, end);
                removeSelection = false;
            }
        }
    }

    public Boolean isBold(SpannableStringBuilder ssb, int start, int end) {
        int next;
        boolean continu = true;
        for (int i = start; i < end && continu; i = next) {
            continu = false;
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            StyleSpan[] spans = ssb.getSpans(i, next, StyleSpan.class);
            for (int j = 0; j < spans.length; j++) {
                if (spans[j].getStyle() == Typeface.BOLD) {
                    continu = true;
                }
            }
        }
        if (continu) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isItalic(SpannableStringBuilder ssb, int start, int end) {
        int next;
        boolean continu = true;
        for (int i = start; i < end && continu; i = next) {
            continu = false;
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            StyleSpan[] spans = ssb.getSpans(i, next, StyleSpan.class);
            for (int j = 0; j < spans.length; j++) {
                if (spans[j].getStyle() == Typeface.ITALIC) {
                    continu = true;
                }
            }
        }
        if (continu) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUnder(SpannableStringBuilder ssb, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            UnderlineSpan[] spans = ssb.getSpans(i, next, UnderlineSpan.class);
            if (spans.length == 0) {
                return false;
            }
        }
        return true;
    }

    public void showView() {
        if (!styleOpened) {
            slideViewHeight(style, 0, dpToPx(54), 300);
            styleOpened = true;
            styleCallback.setStyleOpened(true);
        } else {
            slideViewHeight(style, dpToPx(54), 0, 300);
            styleOpened = false;
            styleCallback.setStyleOpened(false);
            styleCallback.closeAction();
        }
    }

    public void slideViewHeight(final View view, int currentHeight, int newHeight,
                                long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().width = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            editMode = false;
            editpost = false;
            hideKeyBoard(announcementE);
            floating.setImageResource(R.drawable.ic_add_black_24dp);
            announcementList.setVisibility(View.VISIBLE);
            scrollViewE.setVisibility(View.GONE);
            closeStyle();
            closeColors();
        } else {
            finish();
            CustomIntent.customType(this, "right-to-left");
        }
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }
}
