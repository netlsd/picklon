package id.co.picklon.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.data.LocalManager;
import id.co.picklon.model.entities.Address;
import id.co.picklon.model.entities.DateTime;
import id.co.picklon.model.entities.Order;
import id.co.picklon.ui.adapter.OrderSpinnerAdapter;
import id.co.picklon.ui.view.PickAddressDialog;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.TimeUtil;
import id.co.picklon.utils.ViewUtils;

import static id.co.picklon.utils.TimeUtil.getDay;
import static id.co.picklon.utils.TimeUtil.getFormatedDate;
import static id.co.picklon.utils.TimeUtil.getFormatedTime;
import static id.co.picklon.utils.TimeUtil.getHour;
import static id.co.picklon.utils.TimeUtil.getMinutes;
import static id.co.picklon.utils.TimeUtil.getMonth;
import static id.co.picklon.utils.TimeUtil.getYear;

public class NewOrderActivity extends ToolbarActivity {
    private static final int MAX_WEIGHT = 15;
    private static final int MIN_WEIGHT = 3;

    private int weight = 0;
    private int meters = 0;
    private Context mContext;
    private OrderSpinnerAdapter orderSpinnerAdapter;
    private DateTime pickUpDateTime = new DateTime();
    private DateTime deliverDateTime = new DateTime();
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private PickAddressDialog pickAddressDialog;
    private Address address;

    @BindView(R.id.spinner)
    AppCompatSpinner spinnerView;
    @BindView(R.id.new_order_minus)
    ImageView minusView;
    @BindView(R.id.new_order_weight)
    TextView weightView;
    @BindView(R.id.new_order_weight_unit)
    TextView weightUnitView;
    @BindView(R.id.new_order_plus)
    ImageView plusView;
    @BindView(R.id.new_order_carpet_minus)
    ImageView carpetMinusView;
    @BindView(R.id.new_order_wash_checkox)
    AppCompatCheckBox washCheckbox;
    @BindView(R.id.new_order_carpet_checkbox)
    AppCompatCheckBox carpetCheckbox;
    @BindView(R.id.new_order_carpet_icon)
    ImageView carpetIconView;
    @BindView(R.id.new_order_meters)
    TextView metersView;
    @BindView(R.id.new_order_meters_unit)
    TextView metersUnitView;
    @BindView(R.id.new_order_carpet_plus)
    ImageView carpetPlusView;
    @BindView(R.id.new_order_address)
    AppCompatEditText addressView;
    @BindView(R.id.pick_up_date)
    TextView pickDateView;
    @BindView(R.id.pick_up_time)
    TextView pickTimeView;
    @BindView(R.id.deliver_date)
    TextView deliverDateView;
    @BindView(R.id.deliver_time)
    TextView deliverTimeView;
    @BindView(R.id.special_requirements)
    AppCompatEditText requirementsView;
    @BindView(R.id.send_order)
    Button sendOrderView;
    @BindView(R.id.new_order_address_icon)
    ImageView addressIconView;

    @Inject
    LocalManager localManager;
    @Inject
    DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        activityComponent().inject(this);
        mContext = this;

        initUi();
        initBroadcast();
        loadDefaultAddress();
    }

    private void loadDefaultAddress() {
        dataSource.getAddress()
                .retry(3)
                .subscribe(this::handleAddress, Throwable::printStackTrace);
    }

    private void handleAddress(List<Address> addressList) {
        pickAddressDialog.setAddressList(addressList);
        for (Address address : addressList) {
            if (address.getDefaulted() == 1) {
                localManager.saveDefaultAddress(address);
                setAddress(address);
                return;
            }
        }
        localManager.removeDefaultAddress();
    }

    private void initUi() {
        pickAddressDialog = new PickAddressDialog(mContext);
        orderSpinnerAdapter = new OrderSpinnerAdapter(this);
        spinnerView.setAdapter(orderSpinnerAdapter);
        spinnerView.setSelection(orderSpinnerAdapter.getCount());

        minusView.setOnClickListener(view1 -> subWeight());
        plusView.setOnClickListener(view1 -> addWeight());

        carpetMinusView.setOnClickListener(view1 -> subMeters());
        carpetPlusView.setOnClickListener(view1 -> addMeters());

        pickDateView.setOnClickListener(view -> getDataPickerDialog(new PickDateSetListener(), 0).show());
        pickTimeView.setOnClickListener(view -> getTimePickerDialog(new PickTimeSetListener(), 2).show());
        deliverDateView.setOnClickListener(view -> getDataPickerDialog(new DeliverDateSetListener(), 2).show());
        deliverTimeView.setOnClickListener(view -> getTimePickerDialog(new DeliverTimeSetListener(), 2).show());

        sendOrderView.setOnClickListener(view1 -> {
            if (isValid()) {
                OrderActivity.start(mContext, toNewOrder());
            }
        });

        WashCheckedChangeListener washCheckedChangeListener = new WashCheckedChangeListener();
        washCheckbox.setOnCheckedChangeListener(washCheckedChangeListener);
        washCheckedChangeListener.onCheckedChanged(washCheckbox, false);

        CarpetCheckedChangeListener carpetCheckedChangeListener = new CarpetCheckedChangeListener();
        carpetCheckbox.setOnCheckedChangeListener(carpetCheckedChangeListener);
        carpetCheckedChangeListener.onCheckedChanged(carpetCheckbox, false);

        addressIconView.setOnClickListener(view -> showPickAddressDialog());
        addressView.setOnClickListener(view -> showPickAddressDialog());

        pickAddressDialog.setOnClickAddressListener(this::setAddress);
    }

    private DatePickerDialog getDataPickerDialog(DatePickerDialog.OnDateSetListener listener, int addDay) {
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.TimePickDialogTheme, listener, getYear(), getMonth(), getDay() + addDay);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000 + TimeUnit.MILLISECONDS.convert(addDay, TimeUnit.DAYS));
        return dialog;
    }

    private TimePickerDialog getTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, int addHour) {
//        RangeTimePickerDialog dialog = new RangeTimePickerDialog(this, listener, getHour(), getMinutes(), true);
//        dialog.setMin(getHour(), getMinutes());
//        return dialog;
        return new TimePickerDialog(this, R.style.TimePickDialogTheme, listener, getHour() + addHour, getMinutes(), true);
    }

    private void showPickAddressDialog() {
        pickAddressDialog.show();
    }

    private class WashCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                minusView.setEnabled(false);
                minusView.setImageResource(R.drawable.ic_minus_disabled);
                plusView.setEnabled(true);
                plusView.setImageResource(R.drawable.ic_plus);
                weightView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                weightUnitView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                weight = MIN_WEIGHT;
                showWeightView();
                spinnerView.setEnabled(true);
            } else {
                minusView.setEnabled(false);
                minusView.setImageResource(R.drawable.ic_minus_disabled);
                plusView.setEnabled(false);
                plusView.setImageResource(R.drawable.ic_plus_disabled);
                weightView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                weightUnitView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                spinnerView.setSelection(orderSpinnerAdapter.getCount());
                weight = 0;
                showWeightView();
                spinnerView.setEnabled(false);
            }
        }
    }

    private class CarpetCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                carpetMinusView.setEnabled(true);
                carpetMinusView.setImageResource(R.drawable.ic_minus);
                carpetPlusView.setEnabled(true);
                carpetPlusView.setImageResource(R.drawable.ic_plus);
                metersView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                metersUnitView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                carpetIconView.setImageResource(R.drawable.ic_carpet);
            } else {
                carpetMinusView.setEnabled(false);
                carpetMinusView.setImageResource(R.drawable.ic_minus_disabled);
                carpetPlusView.setEnabled(false);
                carpetPlusView.setImageResource(R.drawable.ic_plus_disabled);
                metersView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                metersUnitView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                carpetIconView.setImageResource(R.drawable.ic_carpet_disabled);
                meters = 0;
                showMetersView();
            }
        }
    }

    private Order toNewOrder() {
        Order order = new Order();
        order.setWashType(spinnerView.getSelectedItemPosition());
        order.setWashWeight(weight);
        if (carpetCheckbox.isChecked()) {
            order.setCarpet(true);
        }
        order.setMeters(meters);
        order.setAddress(address);
        order.setPickTime(pickUpDateTime);
        order.setdTime(deliverDateTime);
        order.setRequirements(requirementsView.getText().toString());
        return order;
    }

    private Boolean isValid() {
        if (!washCheckbox.isChecked() && !carpetCheckbox.isChecked()) {
            ViewUtils.showDialog(mContext, R.string.wash_warning);
            return false;
        }

        if (washCheckbox.isChecked() && spinnerView.getSelectedItemPosition() == spinnerView.getAdapter().getCount()) {
            ViewUtils.showDialog(mContext, R.string.wash_type_warning);
            return false;
        }

        if (washCheckbox.isChecked() && weight == 0) {
            ViewUtils.showDialog(mContext, R.string.wash_weight_warning);
            return false;
        }

        if (carpetCheckbox.isChecked() && meters == 0) {
            ViewUtils.showDialog(mContext, R.string.wash_meters_warning);
            return false;
        }

        if (TextUtils.isEmpty(addressView.getText().toString().trim())) {
            ViewUtils.showDialog(mContext, R.string.address_warning);
            return false;
        }

        if (TextUtils.isEmpty(pickDateView.getText()) || TextUtils.isEmpty(pickTimeView.getText())) {
            ViewUtils.showDialog(mContext, R.string.pickup_warning);
            return false;
        }

        if (TextUtils.isEmpty(deliverDateView.getText()) || TextUtils.isEmpty(deliverTimeView.getText())) {
            ViewUtils.showDialog(mContext, R.string.deliver_warning);
            return false;
        }

        return true;
    }

    private void addMeters() {
        ++meters;
        showMetersView();
    }

    private void subMeters() {
        if (meters == 0) {
            return;
        }
        --meters;
        showMetersView();
    }

    private void showMetersView() {
        metersView.setText(String.valueOf(meters));
    }

    private void addWeight() {
        minusView.setImageResource(R.drawable.ic_minus);
        plusView.setImageResource(R.drawable.ic_plus);
        minusView.setEnabled(true);

        ++weight;
        showWeightView();
        if (weight == MAX_WEIGHT){
            plusView.setImageResource(R.drawable.ic_plus_disabled);
            plusView.setEnabled(false);
        }
    }

    private void subWeight() {
        plusView.setImageResource(R.drawable.ic_plus);
        minusView.setImageResource(R.drawable.ic_minus);
        plusView.setEnabled(true);

        --weight;
        showWeightView();
        if (weight == MIN_WEIGHT) {
            minusView.setImageResource(R.drawable.ic_minus_disabled);
            minusView.setEnabled(false);
        }
    }

    private void showWeightView() {
        weightView.setText(String.valueOf(weight));
    }

    private class PickDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            pickUpDateTime.setYearMonthDay(year, month, day);
            if (isCorrectTime()) {
                pickDateView.setText(getFormatedDate(pickUpDateTime));
            }
        }
    }

    private class PickTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
            pickUpDateTime.setHourMinutes(hour, minutes);
            if (isCorrectTime()) {
                pickTimeView.setText(getFormatedTime(pickUpDateTime));
            }
        }
    }

    private class DeliverDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            deliverDateTime.setYearMonthDay(year, month, day);
            if (isCorrectTime()) {
                deliverDateView.setText(getFormatedDate(deliverDateTime));
            }
        }
    }

    private class DeliverTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
            deliverDateTime.setHourMinutes(hour, minutes);
            if (isCorrectTime()) {
                deliverTimeView.setText(getFormatedTime(deliverDateTime));
            }
        }
    }

    private boolean isCorrectTime() {
        int pYear = pickUpDateTime.getYear();
        int pMonth = pickUpDateTime.getMonth();
        int pDay = pickUpDateTime.getDay();
        int pHour = pickUpDateTime.getHour();
        int pMinute = pickUpDateTime.getMinutes();

        int dYear = deliverDateTime.getYear();
        int dMonth = deliverDateTime.getMonth();
        int dDay = deliverDateTime.getDay();
        int dHour = deliverDateTime.getHour();
        int dMinute = deliverDateTime.getMinutes();

        if (pHour != 0) {
            if (pHour - 2 < TimeUtil.getHour() && isSameDay(pYear, pMonth, pDay)) {
                ViewUtils.showDialog(this, R.string.after_two_hour);
                pickUpDateTime.setHourMinutes(0, 0);
                pickTimeView.setText("");
                return false;
            }

            if (pHour < 10 || pHour >= 20) {
                ViewUtils.showDialog(this, R.string.open_hour_warning);
                pickUpDateTime.setHourMinutes(0, 0);
                return false;
            }
        }

        if (dHour != 0) {
            if (dHour < 10 || dHour >= 20) {
                ViewUtils.showDialog(this, R.string.open_hour_warning);
                deliverDateTime.setHourMinutes(0, 0);
                return false;
            }
        }

        if (pYear == 0 || dYear == 0 || pHour == 0 || dHour == 0) {
            return true;
        }

        Calendar pCal = Calendar.getInstance();
        pCal.set(pYear, pMonth, pDay, pHour, pMinute);
        Calendar dCal = Calendar.getInstance();
        dCal.set(dYear, dMonth, dDay, dHour, dMinute);
        long diff = dCal.getTime().getTime() - pCal.getTime().getTime();
        long between = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (between < 2 || between > 7) {
            ViewUtils.showDialog(this, R.string.time_interval);
            return false;
        }

        return true;
    }

    private boolean isSameDay(int year, int month, int day) {
        return day == getDay() && month == getMonth() && year == getYear();
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Const.UPDATE_ADDRESS.equals(intent.getAction())) {
                Address address = intent.getParcelableExtra(Const.EXTRA_ADDRESS);
                setAddress(address);
                pickAddressDialog.dismiss();
                dataSource.getAddress().retry(3)
                        .subscribe(addresses -> pickAddressDialog.setAddressList(addresses), Throwable::printStackTrace);
            } else if (Const.ORDER_COMPLETED.equals(intent.getAction())) {
                finish();
            }
        }
    }

    private void setAddress(Address address) {
        this.address = address;
        addressView.setText(address.getAddress());
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ORDER_COMPLETED);
        intentFilter.addAction(Const.UPDATE_ADDRESS);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
