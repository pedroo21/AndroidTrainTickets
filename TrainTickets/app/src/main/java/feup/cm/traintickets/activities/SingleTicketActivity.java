package feup.cm.traintickets.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import feup.cm.traintickets.BaseActivity;
import feup.cm.traintickets.R;
import feup.cm.traintickets.models.TicketModel;
import feup.cm.traintickets.util.QREncryption;
import se.simbio.encryption.Encryption;

public class SingleTicketActivity extends BaseActivity {

    private TextView originView;
    private TextView destinationView;
    private TextView dateView;
    private TextView timeView;
    private TextView seatView;

    private ImageView qrCodeImageView;
    private Bitmap bitmap;
    private View contentView;
    private View progressBar;

    public final static int QRCODE_WIDTH = 500;
    private TicketModel ticket;

    private boolean authCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_single);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        originView = (TextView) findViewById(R.id.ticket_origin);
        destinationView = (TextView) findViewById(R.id.ticket_destination);
        dateView = (TextView) findViewById(R.id.ticket_date_value);
        timeView = (TextView) findViewById(R.id.ticket_hour_value);
        seatView = (TextView) findViewById(R.id.ticket_seat_value);

        contentView = findViewById(R.id.card_view);
        progressBar = findViewById(R.id.ticket_progress);
        qrCodeImageView = (ImageView) findViewById(R.id.imageView2);

        Intent ticketIntent = getIntent();
        try {
            ticket = new Gson().fromJson(ticketIntent.getStringExtra("TICKET_MODEL"),
                    TicketModel.class);
            this.authCheck = ticketIntent.getBooleanExtra("TICKET_ONLINE", false);
        }
        catch(JsonParseException ignored) { }

        if(ticket == null) {
            Toast.makeText(this, "Invalid ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        SimpleDateFormat timef = new SimpleDateFormat("hh:mm", Locale.UK);
        String ticketDate = datef.format(ticket.getTicketDate());
        try {
            originView.setText(ticket.getDepartureStation().getStationName());
            destinationView.setText(ticket.getArrivalStation().getStationName());
            dateView.setText(ticketDate);

            if(ticket.getDepartureTime() != null) {
                timeView.setText(timef.format(ticket.getDepartureTime()));
            }
            seatView.setText(ticket.getSeatModel().getSeatNumber());
        }
        catch(NullPointerException e) {
            // Data not available, but try to generate QR code
            e.printStackTrace();
        }

        String textToEncode = ticket.getUniqueId() + ";#" +
                              ticketDate + ";#" +
                              ticket.getArrivalStation().getId() + ";#" +
                              ticket.getDepartureStation().getId() + ";#" +
                              ticket.getIsUsed() + ";#" +
                              ticket.getTrip().getId();

        String crypt = null;

        //if (ticket.getTicketDate().after(Calendar.getInstance().getTime())) {
            try {
                Encryption enc = QREncryption.getInstance();
                crypt = enc.encryptOrNull(textToEncode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (crypt != null)
                generateQR(crypt);
            else
                Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        //}
        //else {
        //    Toast.makeText(this, "Ticket has expired QR Code generation", Toast.LENGTH_LONG).show();
        //}
    }

    @Override
    protected int getBottomNavId() {
        return R.id.action_tickets;
    }

    protected ViewGroup getMainLayout() {
        return (ConstraintLayout) findViewById(R.id.ticket_main_layout);
    }

    @Override
    public boolean authCheck() {
        return authCheck;
    }

    private void generateQR(final String textToEncode) {
        final AsyncTask<Void, Void, Boolean> runner = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    bitmap = textToImageEncode(textToEncode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeImageView.setImageBitmap(bitmap);
                            showProgress(false);
                        }
                    });
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        showProgress(true);
        runner.execute((Void) null);
    }

    private Bitmap textToImageEncode(String editTextValue) throws Exception {
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(
                    editTextValue,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRCODE_WIDTH, QRCODE_WIDTH, null
            );
        } catch (IllegalArgumentException iae) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int i = 0; i < bitMatrixHeight; i++) {
            int offset = i * bitMatrixWidth;

            for (int j = 0; j < bitMatrixWidth; j++) {
                pixels[offset + j] = bitMatrix.get(j, i)
                        ? getResources().getColor(R.color.QRCodeBlack)
                        : getResources().getColor(R.color.QRCodeWhite);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        contentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
