package com.example.org.aemwifiprinterdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.aem.api.AEMPrinter;
import com.aem.api.AEMScrybeDevice;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonConnect, mButtonPrintSamples;
    private EditText mEditTextIpAddress;
    private String mIpAddress="";
    MyClient mMyClient =null;
    private final String mText ="";
    private AEMScrybeDevice mAemScrybeDevice;
    AEMPrinter mAemPrinter = null;
    private Socket mSocket=null;
    private PrintWriter mPrintOut = null;
    private final int mNumChars =32;
    private int mWiFiConnection =0;
    List<PrinterModel> mPrinterModelList = new ArrayList<>();
    private CheckBox mCheckBoxKitchen, mCheckBoxBar, mCheckBoxCash;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        mButtonConnect.setOnClickListener(this);
        mButtonPrintSamples.setOnClickListener(this);
        mCheckBoxKitchen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                onKitchenCheckedChangeListener(isChecked);
            }
        });

        mCheckBoxBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                onBarCheckedChangeListener(isChecked);
            }
        });

        mCheckBoxCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                onCashCheckedChangeListener(isChecked);
            }
        });
    }

    private void onKitchenCheckedChangeListener(final boolean isChecked) {
        if (isChecked){
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i< mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), true, mPrinterModelList.get(i).isBar(), mPrinterModelList.get(i).isCash());
                }
            } else {
                showToastMessage();
            }
        } else {
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i < mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), false, mPrinterModelList.get(i).isBar(), mPrinterModelList.get(i).isCash);
                }
            }
        }
    }

    private void onBarCheckedChangeListener(final boolean isChecked) {
        if (isChecked){
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i< mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), mPrinterModelList.get(i).isKitchen(), true, mPrinterModelList.get(i).isCash());
                }
            } else {
                showToastMessage();
            }
        } else {
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i < mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), mPrinterModelList.get(i).isKitchen(), false, mPrinterModelList.get(i).isCash());
                }
            }
        }
    }

    private void onCashCheckedChangeListener(final boolean isChecked) {
        if (isChecked){
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i< mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), mPrinterModelList.get(i).isKitchen(), mPrinterModelList.get(i).isBar(), true);
                }
            } else {
                showToastMessage();
            }
        } else {
            if (mPrinterModelList.size()>0) {
                for (int i = 0; i < mPrinterModelList.size(); i++) {
                    setBluetoothPrinterList(i, mPrinterModelList.get(i).getIpAddress(), mPrinterModelList.get(i).isKitchen(), mPrinterModelList.get(i).isBar(), false);
                }
            }
        }
    }

    /**
     * Show toast messages
     */
    private void showToastMessage() {
        Toast.makeText(MainActivity.this, R.string.toast_printer_no_connection, Toast.LENGTH_SHORT).show();
    }

    /**
     * Set bluetooth printer connection status
     * @param i
     * @param ipAddress
     * @param isKitchenSelected
     * @param isBarSelected
     * @param isCashSeleceted
     */
    private void setBluetoothPrinterList(final int i, final String ipAddress, final boolean isKitchenSelected, final boolean isBarSelected, final boolean isCashSeleceted) {
        final PrinterModel printerModel = new PrinterModel();
        printerModel.setIpAddress(ipAddress);
        printerModel.setPrinteryType(getString(R.string.text_aem_wifi_printer));
        printerModel.setKitchen(isKitchenSelected);
        printerModel.setBar(isBarSelected);
        printerModel.setCash(isCashSeleceted);
        mPrinterModelList.set(i, printerModel);
    }

    /**
     * Method used to print data on WIFI printer
     */
    private void printDataOnWifiPrinter() {
        byte[] btArr = {0, 0};
        byte[] btArrCR = {'\n', '\n'};
        String ctrlCR  = new String(btArrCR);
        String ctrlChars  = new String(btArr);
        byte[] DBL = {0, 0};
        String ctrlCharsDBL  = new String(DBL);
        String data = " TWO INCH PRINTER: TEST PRINT\n";
        String d =    "_________________________________\n";

        if(mNumChars == 32) {
            mMyClient.sendDataOnSocket(ctrlChars, mPrintOut);
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);

            data = "CODE|DESC|RATE(Rs)|QTY |AMT(Rs)\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);
            data =   "13|ColgateGel |35.00|02|70.00\n"+
                    "29|Pears Soap |25.00|01|25.00\n"+
                    "88|Lux Shower |46.00|01|46.00\n"+
                    "15|Dabur Honey|65.00|01|65.00\n"+
                    "52|Dairy Milk |20.00|10|200.00\n"+
                    "128|Maggie TS |36.00|04|144.00\n"+
                    "_______________________________\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(ctrlCharsDBL, mPrintOut);
            data = "   TOTAL AMOUNT (Rs.)   550.00\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);
            data = "   Thank you! \n";
        } else {
            data = "        THREE INCH PRINTER: TEST PRINT\n";
            d =    "________________________________________________\n";

            mMyClient.sendDataOnSocket(ctrlChars, mPrintOut);
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);

            data = 		  "CODE|   DESCRIPTION   |RATE(Rs)|QTY |AMOUNT(Rs)\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);
            data = " 13 |Colgate Total Gel | 35.00  | 02 |  70.00\n"+
                    " 29 |Pears Soap 250g   | 25.00  | 01 |  25.00\n"+
                    " 88 |Lux Shower Gel 500| 46.00  | 01 |  46.00\n"+
                    " 15 |Dabur Honey 250g  | 65.00  | 01 |  65.00\n"+
                    " 52 |Cadbury Dairy Milk| 20.00  | 10 | 200.00\n"+
                    "128 |Maggie Totamto Sou| 36.00  | 04 | 144.00\n"+
                    "______________________________________________\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(ctrlCharsDBL, mPrintOut);
            data = "          TOTAL AMOUNT (Rs.)   550.00\n";
            mMyClient.sendDataOnSocket(data, mPrintOut);
            mMyClient.sendDataOnSocket(d, mPrintOut);
            data = "     Thank you! \n";
        }
    }

    private void initializeViews() {
        mButtonConnect = (Button) findViewById(R.id.buttonConnect);
        mButtonPrintSamples = (Button) findViewById(R.id.buttonPrintSamples);
        mEditTextIpAddress = (EditText) findViewById(R.id.editTextIpAddress);
        mCheckBoxKitchen = (CheckBox) findViewById(R.id.checkboxkitchen);
        mCheckBoxBar = (CheckBox) findViewById(R.id.checkboxbar);
        mCheckBoxCash = (CheckBox) findViewById(R.id.checkboxcash);
    }

    public void onSuccess(final String response, final Socket socket, final String dstAddress, final int dstPort, final PrintWriter out) {
        mIpAddress = dstAddress;
        mSocket = socket;
        mPrintOut = out;
        mEditTextIpAddress.setText(mIpAddress);
        mWiFiConnection = 1;
        mButtonConnect.setText(R.string.text_disconnect);
        showAlert(getString(R.string.toast_printer_connection)+" "+response);
        setPrinterList(mIpAddress);
    }

    /**
     * Set default printer status disconnected for kitchen, bar and cash
     * @param mIpAddress
     */
    private void setPrinterList(final String mIpAddress) {
        final PrinterModel printerModel = new PrinterModel();
        printerModel.setIpAddress(mIpAddress);
        printerModel.setPrinteryType(getString(R.string.text_aem_wifi_printer));
        printerModel.setKitchen(false);
        printerModel.setBar(false);
        printerModel.setCash(false);
        mPrinterModelList.add(printerModel);
    }

    /**
     * Method used to call on error of printer connection
     * @param response
     */
    public void onError(final String response) {
        mWiFiConnection = 0;
        showAlert(getString(R.string.toast_try_again)+" "+response);
    }

    /**
     * Method used to show alert messages
     * @param msg
     */
    private void showAlert(final String msg) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
                return;
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            /**
             * to connect printer
             */
            case R.id.buttonConnect:
                if (!mEditTextIpAddress.getText().toString().equalsIgnoreCase("")) {
                    if (mWiFiConnection ==0) {
                        mIpAddress = mEditTextIpAddress.getText().toString().trim();
                        mMyClient = new MyClient(mIpAddress, MainActivity.this);
                        mMyClient.configSocket(mIpAddress);
                        mWiFiConnection =1;
                        new MyClient(mIpAddress, MainActivity.this).execute();
                    } else {
                        mMyClient = null;
                        mWiFiConnection = 0;
                        mButtonConnect.setText(R.string.text_connect);
                        showAlert(getString(R.string.alert_disconnect));
                    }
                } else {
                    showAlert(getString(R.string.toast_enter_ip_address));
                }
                break;

            /**
             * Print sample on print receipt
             */
            case R.id.buttonPrintSamples:
                if (mMyClient == null) {
                    showAlert(getString(R.string.toast_printer_no_connection));
                    return;
                }
                printDataOnWifiPrinter();
                break;
        }
    }
}
