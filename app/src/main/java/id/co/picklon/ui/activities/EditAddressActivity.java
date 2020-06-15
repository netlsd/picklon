package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.data.LocalManager;
import id.co.picklon.model.entities.Address;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.Tool;
import id.co.picklon.utils.ViewUtils;


public class EditAddressActivity extends ToolbarActivity {
    private static final String ADDRESS = "address";

    private int PLACE_PICKER_REQUEST = 1;

    //    @BindView(R.id.edit_address_city)
//    AppCompatEditText cityView;
//    @BindView(R.id.edit_address_area)
//    AppCompatEditText areaView;
//    @BindView(R.id.edit_address_street)
//    AppCompatEditText streetView;
    @BindView(R.id.edit_address_map)
    AppCompatEditText mapView;
    @BindView(R.id.edit_address_checkbox)
    AppCompatCheckBox checkBoxView;
    @BindView(R.id.edit_address_delete)
    Button deleteView;
    @BindView(R.id.edit_address_save)
    Button saveView;
    @BindView(R.id.edit_address_choose)
    Button chooseBtn;

    @Inject
    DataSource dataSource;
    @Inject
    LocalManager localManager;

    private double lat = 0;
    private double lon = 0;

    private String street;
    private Address extraAddress;
    private Address address;

    public static void start(Context context, Address address) {
        Intent intent = new Intent(context, EditAddressActivity.class);
        intent.putExtra(ADDRESS, address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        activityComponent().inject(this);

        initUi();
    }

    private void initUi() {
        setToolbarTitle(R.string.edit_address);

        setupAddress();

        chooseBtn.setOnClickListener(view -> chooseFromMap());
        deleteView.setOnClickListener(view -> clearAll());
        saveView.setOnClickListener(view -> saveAddress());
    }

    private void setupAddress() {
        extraAddress = getIntent().getParcelableExtra(ADDRESS);

        if (extraAddress != null) {
            mapView.setText(extraAddress.getAddress());
            if (extraAddress.getDefaulted() == 1) {
                checkBoxView.setChecked(true);
            }
            lat = extraAddress.getLatitude();
            lon = extraAddress.getLongitude();
        }
    }

    private void saveAddress() {
        street = mapView.getText().toString().trim();
        if (TextUtils.isEmpty(street)) {
            ViewUtils.showDialog(this, R.string.address_warning);
            return;
        }

        if (lat == 0 || lon == 0) {
            ViewUtils.showDialog(this, R.string.choose_map_warning);
            return;
        }

        if (extraAddress == null) {
            address = toAddress(new Address());
            dataSource.addAddress(address).subscribe(new ProgressSubscriber<>(this, this::onSuccess));
        } else {
            address = toAddress(extraAddress);
            dataSource.editAddress(address).subscribe(new ProgressSubscriber<>(this, this::onSuccess));
        }
    }

    private void onSuccess(Object object) {
        Address address;
        Toast.makeText(this, R.string.save_success, Toast.LENGTH_LONG).show();

        if (object instanceof Address) {
            address = (Address) object;
            this.address.setId(address.getId());
        }

        Intent intent = new Intent(Const.UPDATE_ADDRESS);
        intent.putExtra(Const.EXTRA_ADDRESS, this.address);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        if (checkBoxView.isChecked()) {
            localManager.saveDefaultAddress(this.address);
        }
        finish();
    }

    private Address toAddress(Address address) {
        address.setAddress(street);
        address.setLatitude(lat);
        address.setLongitude(lon);
        address.setDefaulted(checkBoxView.isChecked() ? 1 : 0);
        return address;
    }

    private void chooseFromMap() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);

        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.showErrorDialogFragment(code, this, null, 1, DialogInterface::cancel);
            return;
        }

        if (Tool.isGPSDisabled(this)) {
            ViewUtils.showDialog(this, R.string.open_gps, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
            return;
        }

        final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mapView.setText(place.getAddress());
                LatLng latLng = place.getLatLng();
                lat = latLng.latitude;
                lon = latLng.longitude;
            }
        }
    }

    private void clearAll() {
//        cityView.setText("");
//        areaView.setText("");
//        streetView.setText("");
        mapView.setText("");
        checkBoxView.setChecked(false);
    }
}
