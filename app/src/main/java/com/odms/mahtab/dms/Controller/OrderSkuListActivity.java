package com.odms.mahtab.dms.Controller;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.odms.mahtab.dms.Adapter.OrderSkuListAdapter;
import com.odms.mahtab.dms.Database.LocalQuery.tbld_Sku_Local;
import com.odms.mahtab.dms.Database.LocalQuery.temp_order_line;
import com.odms.mahtab.dms.Model.M_SKU;
import com.odms.mahtab.dms.Model.M_temp_order_line;
import com.odms.mahtab.dms.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderSkuListActivity extends AppCompatActivity {
    ListView listView;
    List<M_SKU> skuArrayList = new ArrayList<>();
    int subrouteid, Todayvisit;
    temp_order_line order_line;

    // Search EditText
    EditText inputSearch;
    OrderSkuListAdapter adapter;
    //Kpi

    TextView SC, Memo, NotOrder, Pending, Ordercs, TLSD, LPSC, Drop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sku_list);
        order_line=new temp_order_line(getApplicationContext());
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();
        TextView tvOutlet = view.findViewById(R.id.actionbar);
        tvOutlet.setText("Order Sku List");

        ImageButton btnBack = (ImageButton) view.findViewById(R.id.action_back_btn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton homeBack = (ImageButton) view.findViewById(R.id.action_home_btn);
        homeBack.setVisibility(View.GONE);

        Log.e("sku", "SKU LIST");
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setSelected(false);


        listView = (ListView) findViewById(R.id.list_view);
        ListViewShow();


        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


    }


    void ListViewShow() {

        skuArrayList = new ArrayList<>();
        tbld_Sku_Local sku_local = new tbld_Sku_Local(getApplicationContext());

        List<M_SKU> Conts = sku_local.getAllSkulist();

        for (M_SKU Cont : Conts) {
            skuArrayList.add(new M_SKU(Cont.getId(), Cont.getSKUId(), Cont.getPackSize(), Cont.getTP(), Cont.getSKUName(), Cont.getPromo_name()));

        }

        adapter = new OrderSkuListAdapter(this, R.layout.order_sku_listview_row, skuArrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                M_SKU data = skuArrayList.get(position);

                showInputDialog(data.getSKUName(), data.getSKUId(),data.getBatch_id(), data.getSKUlpc(),data.getPackSize(), data.getTP(),data.getMRP());


            }
        });

    }


    protected void showInputDialog(final String Skuname, int skuid, final int batch_id, final int lpsc, final int packsize, final double tp,final double MRP) {
        final int Skuid = skuid;
        final int Packsize = packsize;
        final double PSprice = tp;
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(OrderSkuListActivity.this);
        View promptView = layoutInflater.inflate(R.layout.order_sku_alertbox, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderSkuListActivity.this);

        alertDialogBuilder.setTitle(Skuname);
        alertDialogBuilder.setView(promptView);

        final TextView promo = (TextView) promptView.findViewById(R.id.promoinfo);
        final EditText orderCs = (EditText) promptView.findViewById(R.id.orderCs);
        final EditText orderps = (EditText) promptView.findViewById(R.id.orderPs);
        final EditText Total = (EditText) promptView.findViewById(R.id.totalprice);
        final EditText Tps = (EditText) promptView.findViewById(R.id.totalps);

        Total.setEnabled(false);

        promo.setText("");

        orderCs.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int totalCs = 0;
                int pcs = 0;

                if (!orderCs.getText().toString().trim().equals("")) {
                    totalCs = Integer.parseInt(String.valueOf(orderCs.getText()));
                }

                if (!orderps.getText().toString().trim().equals("")) {
                    pcs = Integer.parseInt(String.valueOf(orderps.getText()));
                }

                double TotalAmount = grandtotal(totalCs, pcs, Packsize, PSprice);

                Total.setText(Double.toString(TotalAmount));

                Tps.setText(Integer.toString((totalCs*Packsize)+pcs));
            }
        });

        orderps.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int totalCs = 0;
                int pcs = 0;

                if (!orderCs.getText().toString().trim().equals("")) {

                    totalCs = Integer.parseInt(String.valueOf(orderCs.getText()));
                }

                if (!orderps.getText().toString().trim().equals("")) {
                    pcs = Integer.parseInt(String.valueOf(orderps.getText()));
                }

                double TotalAmount = grandtotal(totalCs, pcs, Packsize, PSprice);

                Total.setText(Double.toString(TotalAmount));
                Tps.setText(Integer.toString((totalCs*Packsize)+pcs));
            }
        });


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        M_temp_order_line line=new M_temp_order_line();

                        line.setSKUId(Skuid);
                        line.setSKUName(Skuname);
                        line.setBatch_id(batch_id);
                        line.setSKUlpc(lpsc);
                        line.setLinetype(1);
                        line.setPackSize(packsize);
                        line.setTP(tp);
                        line.setMRP(MRP);

                        order_line.insertOrderLine(line);
                        Toast.makeText(getApplicationContext(), "SKU: " + Skuid, Toast.LENGTH_SHORT).show();



                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }

    private double grandtotal(int totalCs, int pcs, int Packsize, double PSprice) {
        int totalpcs = (totalCs * Packsize) + pcs;
        double totalPrice = Math.round(totalpcs * PSprice);
        return totalPrice;
    }

    void addLine(){

    }

}
