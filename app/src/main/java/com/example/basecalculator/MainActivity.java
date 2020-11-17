package com.example.basecalculator;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import maes.tech.intentanim.CustomIntent;
import static com.example.basecalculator.RegisterActivity.SHARED_PREFS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    //Views
    private DrawerLayout drawer;
    private TextView equatiom, result;
    private ListView history;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private GridLayout otherD;
    private RelativeLayout buttons;
    private ImageButton expandButton;
    private RadioButton radioButton, checkB16, checkB10, checkB8, checkB2;

    //Variables
    boolean expande = false;
    Long HisNum = 0L;
    int base = 10, conTyped = 0, height = 0;
    String EqToSave = "";
    ArrayList<history> Histories = new ArrayList<>();

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        String text = radioButton.getText().toString();
        if (isNumeric(text)) {
            int oldBase = base;

            if ("2".compareTo(text) == 0) {
                spinner.setSelection(0);
            }
            if ("8".compareTo(text) == 0) {
                spinner.setSelection(6);
            }
            if ("10".compareTo(text) == 0) {
                spinner.setSelection(8);
            }
            if ("16".compareTo(text) == 0) {
                spinner.setSelection(14);
            }
            base = Integer.parseInt(text);
            equatiom.setText(ConverString(equatiom.getText().toString().trim(), oldBase, base));
            UpdateResult();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        radioGroup.clearCheck();
        if (isNumeric(text)) {
            if ("2".compareTo(text) == 0) {
                checkB2.setChecked(true);
            }
            if ("8".compareTo(text) == 0) {
                checkB8.setChecked(true);
            }
            if ("10".compareTo(text) == 0) {
                checkB10.setChecked(true);
            }
            if ("16".compareTo(text) == 0) {
                checkB16.setChecked(true);
            }
            int oldBase = base;
            base = Integer.parseInt(text);
            equatiom.setText(ConverString(equatiom.getText().toString().trim(), oldBase, base));
            UpdateResult();
        }
    }

    public void expande(View view) {
        if (!expande) {
            expande = true;
            expandButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
            HeightSlideView(otherD, 0, height - dpToPx(20), 150);
        } else {
            expande = false;
            expandButton.setImageResource(R.drawable.ic_expand_less_black_24dp);
            HeightSlideView(otherD, height - dpToPx(20), 0, 150);
        }
    }

    public void AC(View v) {
        equatiom.setText("");
        result.setText("");
        UpdateResult();
        conTyped = 0;
    }

    public void DEL(View v) {
        if (!equatiom.getText().toString().isEmpty()) {
            if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(")") == 0) {
                conTyped++;
            } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("(") == 0) {
                conTyped--;
            }
            equatiom.setText(equatiom.getText().toString().substring(0, equatiom.getText().toString().length() - 1));
            UpdateResult();
        }
    }

    public void con(View v) {
        if (!equatiom.getText().toString().isEmpty()) {
            if (conTyped <= 0) {
                if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(".") == 0) {
                    equatiom.setText(equatiom.getText().toString().substring(0, equatiom.getText().toString().length() - 1) + "(");
                    UpdateResult();
                    conTyped++;
                } else if (isNumber(equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1)) || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(")") == 0) {
                    equatiom.setText(equatiom.getText().toString() + "x(");
                    UpdateResult();
                    conTyped++;
                } else {
                    equatiom.setText(equatiom.getText().toString() + "(");
                    UpdateResult();
                    conTyped++;
                }
            } else {
                if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(".") == 0) {
                    equatiom.setText(equatiom.getText().toString().substring(0, equatiom.getText().toString().length() - 1) + ")");
                    conTyped--;
                    UpdateResult();
                } else if (isNumber(equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1))) {
                    equatiom.setText(equatiom.getText().toString() + ")");
                    conTyped--;
                    UpdateResult();
                } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("(") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("x") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("/") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("+") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("-") == 0) {
                    equatiom.setText(equatiom.getText().toString() + "(");
                    UpdateResult();
                    conTyped++;
                } else {
                    equatiom.setText(equatiom.getText().toString() + ")");
                    UpdateResult();
                    conTyped--;
                }
            }
        } else {
            equatiom.setText(equatiom.getText().toString() + "(");
            UpdateResult();
            conTyped++;
        }
    }

    public void dot(View v) {
        if (equatiom.getText().toString().isEmpty()) {
            equatiom.setText(equatiom.getText().toString() + "0.");
            UpdateResult();
        } else {
            if (isNumber(equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1))) {
                if (checkDot(equatiom.getText().toString())) {
                    equatiom.setText(equatiom.getText().toString() + ".");
                    UpdateResult();
                }
            } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(")") == 0) {
                equatiom.setText(equatiom.getText().toString() + "x0.");
                UpdateResult();
            } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(".") != 0) {
                equatiom.setText(equatiom.getText().toString() + "0.");
                UpdateResult();
            }
        }
    }

    public void div(View v) {
        action("/");
    }

    public void x(View v) {
        action("x");
    }

    public void min(View v) {
        action("-");
    }

    public void plus(View v) {
        action("+");
    }

    public void _0(View v) {
        calculate("0");
    }

    public void _1(View v) {
        calculate("1");
    }

    public void _2(View v) {
        calculate("2");
    }

    public void _3(View v) {
        calculate("3");
    }

    public void _4(View v) {
        calculate("4");
    }

    public void _5(View v) {
        calculate("5");
    }

    public void _6(View v) {
        calculate("6");
    }

    public void _7(View v) {
        calculate("7");
    }

    public void _8(View v) {
        calculate("8");
    }

    public void _9(View v) {
        calculate("9");
    }

    public void A(View v) {
        calculate("A");
    }

    public void B(View v) {
        calculate("B");
    }

    public void C(View v) {
        calculate("C");
    }

    public void D(View v) {
        calculate("D");
    }

    public void E(View v) {
        calculate("E");
    }

    public void F(View v) {
        calculate("F");
    }

    public void G(View v) {
        calculate("G");
    }

    public void H(View v) {
        calculate("H");
    }

    public void I(View v) {
        calculate("I");
    }

    public void J(View v) {
        calculate("J");
    }

    public void K(View v) {
        calculate("K");
    }

    public void L(View v) {
        calculate("L");
    }

    public void M(View v) {
        calculate("M");
    }

    public void N(View v) {
        calculate("N");
    }

    public void O(View v) {
        calculate("O");
    }

    public void P(View v) {
        calculate("P");
    }

    public void Q(View v) {
        calculate("Q");
    }

    public void R(View v) {
        calculate("R");
    }

    public void S(View v) {
        calculate("S");
    }

    public void T(View v) {
        calculate("T");
    }

    public void U(View v) {
        calculate("U");
    }

    public void V(View v) {
        calculate("V");
    }

    public void W(View v) {
        calculate("W");
    }

    public void X(View v) {
        calculate("X");
    }

    public void Y(View v) {
        calculate("Y");
    }

    public void Z(View v) {
        calculate("Z");
    }

    public void equal(View v) {
        if (!EqToSave.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("History").child(Long.toString(HisNum)).child("value").setValue(EqToSave);
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("History").child(Long.toString(HisNum)).child("base").setValue(base);
            String[] arrOfStr = EqToSave.split("=", 2);
            result.setText("");
            equatiom.setText(arrOfStr[1]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        equatiom = findViewById(R.id.Equation);
        result = findViewById(R.id.result);
        history = findViewById(R.id.history);
        radioGroup = findViewById(R.id.groupB);
        checkB16 = findViewById(R.id.checkB16);
        checkB10 = findViewById(R.id.checkB10);
        checkB8 = findViewById(R.id.checkB8);
        checkB2 = findViewById(R.id.checkB2);
        spinner = findViewById(R.id.spinner1);
        otherD = findViewById(R.id.otherD);
        buttons = findViewById(R.id.buttons);
        expandButton = findViewById(R.id.expandButton);


        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                spinner.setSelection(Integer.parseInt(Histories.get(position).getBase())-2);
                if ("2".compareTo(Histories.get(position).getBase()) == 0) {
                    checkB2.setChecked(true);
                }
                if ("8".compareTo(Histories.get(position).getBase()) == 0) {
                    checkB8.setChecked(true);
                }
                if ("10".compareTo(Histories.get(position).getBase()) == 0) {
                    checkB10.setChecked(true);
                }
                if ("16".compareTo(Histories.get(position).getBase()) == 0) {
                    checkB16.setChecked(true);
                }
                base = Integer.parseInt(Histories.get(position).getBase());

                String[] arrOfStr = Histories.get(position).getValue().split("=", 2);
                equatiom.setText(arrOfStr[0]);
                UpdateResult();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HisNum = dataSnapshot.getChildrenCount();
                Histories.clear();
                if (dataSnapshot.getValue() != null) {
                    for (Long i = 0L; i < HisNum; i++) {
                        if(dataSnapshot.child(Long.toString(i)).child("value").getValue()!=null&&dataSnapshot.child(Long.toString(i)).child("base").getValue()!=null)
                        Histories.add(new history(dataSnapshot.child(Long.toString(i)).child("value").getValue().toString(),dataSnapshot.child(Long.toString(i)).child("base").getValue().toString()));
                    }
                }
                historyAdapter adapter = new historyAdapter(getApplicationContext(), R.layout.history, Histories);
                history.setAdapter(adapter);
                scrollMyListViewToBottom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttons.post(new Runnable() {
            @Override
            public void run() {
                height = buttons.getHeight();
            }
        });

        equatiom.setMovementMethod(new ScrollingMovementMethod());

        RadioButton b = (RadioButton) findViewById(R.id.checkB10);
        b.setChecked(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(8);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ClH:
                FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("History").removeValue();
                break;
            case R.id.OthD:
                expande = true;
                expandButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
                HeightSlideView(otherD, 0, height - dpToPx(20), 150);
                break;
            case R.id.Notes:
                startActivity(new Intent(getApplicationContext(), notes.class));
                CustomIntent.customType(this, "left-to-right");
                break;
            case R.id.Ints:
                openCustomTab("https://www.purplemath.com/modules/numbbase.htm");
                break;
            case R.id.LogOut:
                saveData("", "Id");
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
                CustomIntent.customType(this, "right-to-left");
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    void openCustomTab(String url) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
//        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
//        builder.setExitAnimations(this, android.R.anim.slide_in_left,
//                android.R.anim.slide_out_right);
        builder.addDefaultShareMenuItem();
//
//        builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share));
//
//        CustomTabsIntent anotherCustomTab = new CustomTabsIntent.Builder().build();
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
////        builder.setCloseButtonIcon(icon);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_file_upload);
//
//        int requestCode = 100;
//        Intent intent = anotherCustomTab.intent;
//        intent.setData(Uri.parse("http://www.journaldev.com/author/anupam"));
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setActionButton(bitmap, "Android", pendingIntent, true);
        builder.setShowTitle(true);


        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }


    public void calculate(String num) {
        if (base > numberCon(num)) {
            if (!equatiom.getText().toString().isEmpty()) {
                if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(")") == 0) {
                    equatiom.setText(equatiom.getText().toString() + "x" + num);
                    UpdateResult();
                } else {
                    equatiom.setText(equatiom.getText().toString() + num);
                    UpdateResult();
                }
            } else {
                equatiom.setText(equatiom.getText().toString() + num);
                UpdateResult();
            }
        }
    }

    public void action(String string) {
        if (!equatiom.getText().toString().isEmpty()) {
            if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("-") == 0 && equatiom.getText().toString().length() == 1) {
                equatiom.setText(equatiom.getText().toString().substring(0, equatiom.getText().toString().length() - 1));
                UpdateResult();
            } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("(") == 0) {
                if (string.compareTo("x") != 0 && string.compareTo("/") != 0 && string.compareTo("+") != 0) {
                    equatiom.setText(equatiom.getText().toString() + string);
                    UpdateResult();
                }
            } else if (equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("/") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("x") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("-") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo("+") == 0 || equatiom.getText().toString().substring(equatiom.getText().toString().length() - 1).compareTo(".") == 0) {
                equatiom.setText(equatiom.getText().toString().substring(0, equatiom.getText().toString().length() - 1) + string);
                UpdateResult();
            } else {
                equatiom.setText(equatiom.getText().toString() + string);
                UpdateResult();
            }
        } else if (string.compareTo("-") == 0) {
            equatiom.setText(equatiom.getText().toString() + string);
            UpdateResult();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public boolean checkDot(String str) {
        String d = "";
        Matcher m = Pattern.compile("([A-Z0-9.])+").matcher(str);
        while (m.find()) {
            d = m.group(0);
        }
        return !isFound(".", d);
    }

    public String ConverString(String Str, int fromBase, int toBase) {
        Matcher m = Pattern.compile("([A-Z0-9.])+").matcher(Str);
        while (m.find()) {
            if (m.group(0) != null) {
                Str = Str.replaceFirst(m.group(0), baseConvertor(m.group(0), fromBase, toBase));
            }
        }
        return Str;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumericD(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            if (str.compareTo("A") == 0) {
                return true;
            } else if (str.compareTo("B") == 0) {
                return true;
            } else if (str.compareTo("C") == 0) {
                return true;
            } else if (str.compareTo("D") == 0) {
                return true;
            } else if (str.compareTo("E") == 0) {
                return true;
            } else if (str.compareTo("F") == 0) {
                return true;
            } else if (str.compareTo("G") == 0) {
                return true;
            } else if (str.compareTo("H") == 0) {
                return true;
            } else if (str.compareTo("I") == 0) {
                return true;
            } else if (str.compareTo("J") == 0) {
                return true;
            } else if (str.compareTo("K") == 0) {
                return true;
            } else if (str.compareTo("L") == 0) {
                return true;
            } else if (str.compareTo("M") == 0) {
                return true;
            } else if (str.compareTo("N") == 0) {
                return true;
            } else if (str.compareTo("O") == 0) {
                return true;
            } else if (str.compareTo("P") == 0) {
                return true;
            } else if (str.compareTo("Q") == 0) {
                return true;
            } else if (str.compareTo("R") == 0) {
                return true;
            } else if (str.compareTo("S") == 0) {
                return true;
            } else if (str.compareTo("T") == 0) {
                return true;
            } else if (str.compareTo("U") == 0) {
                return true;
            } else if (str.compareTo("V") == 0) {
                return true;
            } else if (str.compareTo("W") == 0) {
                return true;
            } else if (str.compareTo("X") == 0) {
                return true;
            } else if (str.compareTo("Y") == 0) {
                return true;
            } else if (str.compareTo("Z") == 0) {
                return true;
            }
            return false;
        }
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public void UpdateResult() {
        if (!equatiom.getText().toString().isEmpty()) {
            String toCal = equatiom.getText().toString().replace("x", "*");
            if (isFound("*", toCal) || isFound("/", toCal) || isFound("+", toCal) || isFound("-", toCal)) {
                try {
                    String res = ConverString(Double.toString(round(eval(ConverString(toCal, base, 10)), 10)), 10, base);
                    EqToSave = equatiom.getText().toString() + "=" + res;
                    result.setText("=" + res);
                } catch (Exception e) {
                    EqToSave = "";
                    result.setText("");
                }
            } else if (isNumericD(ConverString(toCal, base, 10))) {
                EqToSave = equatiom.getText().toString() + "=" + equatiom.getText().toString();
                result.setText("=" + equatiom.getText().toString());
            } else {
                EqToSave = "";
                result.setText("");
            }
        }else{
            EqToSave = "";
            result.setText("");
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String baseConvertor(String inputNo, int fromBase, int toBase) {
        String digits = "";
        if (isFound(".", inputNo)) {
            String[] arrOfStr = inputNo.split("\\.");

            inputNo = arrOfStr[0];
            if (arrOfStr.length > 1) {
                digits = arrOfStr[1];
                digits = from10ToAny(fromAnyTo10(digits, fromBase), toBase);
            }
        }

        try {
            if (!digits.isEmpty()) {
                return Integer.toString(Integer.parseInt(inputNo, fromBase), toBase).toUpperCase() + "." + digits;
            } else {
                return Integer.toString(Integer.parseInt(inputNo, fromBase), toBase).toUpperCase();
            }
        } catch (Exception e) {
            if (isFound("Number is too big", equatiom.getText().toString())) {
                return "";
            } else {
                return "number is too big";
            }
        }
    }

    public double fromAnyTo10(String str, int from) {
        double digits = 0;
        for (int i = 0; i < str.length(); i++) {
            digits += (numberCon(str.substring(i, i + 1)) / (Math.pow(from, (i + 1))));
        }
        return digits;
    }

    public String from10ToAny(double digits, int to) {
        String digitString = "";
        int counter = 0;
        while (digits > 0 && counter <= 10) {
            digits = digits * ((double) to);
            if (digits >= 1) {
                int num = (int) digits;
                digits -= num;
                digitString += LetterCon(num);
            } else {
                digitString += "0";
            }
            counter++;
        }
        return digitString;
    }

    public String LetterCon(int num) {
        if (num > 9) {
            if (num == 10) {
                return "A";
            } else if (num == 11) {
                return "B";
            } else if (num == 12) {
                return "C";
            } else if (num == 13) {
                return "D";
            } else if (num == 14) {
                return "E";
            } else if (num == 15) {
                return "F";
            } else if (num == 16) {
                return "G";
            } else if (num == 17) {
                return "H";
            } else if (num == 18) {
                return "I";
            } else if (num == 19) {
                return "J";
            } else if (num == 20) {
                return "K";
            } else if (num == 21) {
                return "L";
            } else if (num == 22) {
                return "M";
            } else if (num == 23) {
                return "N";
            } else if (num == 24) {
                return "O";
            } else if (num == 25) {
                return "P";
            } else if (num == 26) {
                return "Q";
            } else if (num == 27) {
                return "R";
            } else if (num == 28) {
                return "S";
            } else if (num == 29) {
                return "T";
            } else if (num == 30) {
                return "U";
            } else if (num == 31) {
                return "V";
            } else if (num == 32) {
                return "W";
            } else if (num == 33) {
                return "X";
            } else if (num == 34) {
                return "Y";
            } else if (num == 35) {
                return "Z";
            }
        }
        return num + "";
    }

    public double numberCon(String let) {
        if (isFound("A", let)) {
            return 10;
        } else if (isFound("B", let)) {
            return 11;
        } else if (isFound("C", let)) {
            return 12;
        } else if (isFound("D", let)) {
            return 13;
        } else if (isFound("E", let)) {
            return 14;
        } else if (isFound("F", let)) {
            return 15;
        } else if (isFound("G", let)) {
            return 16;
        } else if (isFound("H", let)) {
            return 17;
        } else if (isFound("I", let)) {
            return 18;
        } else if (isFound("J", let)) {
            return 19;
        } else if (isFound("K", let)) {
            return 20;
        } else if (isFound("L", let)) {
            return 21;
        } else if (isFound("M", let)) {
            return 22;
        } else if (isFound("N", let)) {
            return 23;
        } else if (isFound("O", let)) {
            return 24;
        } else if (isFound("P", let)) {
            return 25;
        } else if (isFound("Q", let)) {
            return 26;
        } else if (isFound("R", let)) {
            return 27;
        } else if (isFound("S", let)) {
            return 28;
        } else if (isFound("T", let)) {
            return 29;
        } else if (isFound("U", let)) {
            return 30;
        } else if (isFound("V", let)) {
            return 31;
        } else if (isFound("W", let)) {
            return 32;
        } else if (isFound("X", let)) {
            return 33;
        } else if (isFound("Y", let)) {
            return 34;
        } else if (isFound("Z", let)) {
            return 35;
        } else {
            try {
                return Double.parseDouble(let);
            } catch (Exception e) {

            }
        }
        return 0;
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

    public void HeightSlideView(final View view, int currentHeight, int newHeight, long duration) {

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

    public boolean isFound(String p, String hph) {
        boolean Found = hph.indexOf(p) != -1 ? true : false;
        return Found;
    }

    private void scrollMyListViewToBottom() {
        history.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                history.setSelection(history.getCount() - 1);
            }
        });
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

