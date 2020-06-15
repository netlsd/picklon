package id.co.picklon.injector.components;

import dagger.Component;
import id.co.picklon.MainActivity;
import id.co.picklon.injector.Activity;
import id.co.picklon.injector.modules.ActivityModule;
import id.co.picklon.ui.activities.AboutUsActivity;
import id.co.picklon.ui.activities.AccountRecordActivity;
import id.co.picklon.ui.activities.ChangeMobileActivity;
import id.co.picklon.ui.activities.CouponActivity;
import id.co.picklon.ui.activities.DeliverActivity;
import id.co.picklon.ui.activities.EditAddressActivity;
import id.co.picklon.ui.activities.FeedbackActivity;
import id.co.picklon.ui.activities.HomeActivity;
import id.co.picklon.ui.activities.InboxActivity;
import id.co.picklon.ui.activities.ItemListActivity;
import id.co.picklon.ui.activities.LoginActivity;
import id.co.picklon.ui.activities.MobileNumberActivity;
import id.co.picklon.ui.activities.MyAddressActivity;
import id.co.picklon.ui.activities.NewOrderActivity;
import id.co.picklon.ui.activities.OrderActivity;
import id.co.picklon.ui.activities.OrderSuccessActivity;
import id.co.picklon.ui.activities.PartnerActivity;
import id.co.picklon.ui.activities.PaymentActivity;
import id.co.picklon.ui.activities.RegisterActivity;
import id.co.picklon.ui.activities.RegulationActivity;
import id.co.picklon.ui.activities.ResetPasswordActivity;
import id.co.picklon.ui.activities.SettingActivity;
import id.co.picklon.ui.activities.SplashActivity;
import id.co.picklon.ui.activities.TermsAcitivity;
import id.co.picklon.ui.fragments.AllOrderFragment;
import id.co.picklon.ui.fragments.CanceledOrderFragment;
import id.co.picklon.ui.fragments.CommonListFragment;
import id.co.picklon.ui.view.PickAddressDialog;

@Activity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
    void inject(RegisterActivity registerActivity);
    void inject(LoginActivity loginActivity);
    void inject(SplashActivity splashActivity);
    void inject(HomeActivity homeActivity);
    void inject(ResetPasswordActivity resetPasswordActivity);
    void inject(ChangeMobileActivity changeMobileActivity);
    void inject(MobileNumberActivity mobileNumberActivity);
    void inject(RegulationActivity regulationActivity);
    void inject(TermsAcitivity termsAcitivity);
    void inject(AboutUsActivity aboutUsActivity);
    void inject(PartnerActivity partnerActivity);
    void inject(EditAddressActivity editAddressActivity);
    void inject(MyAddressActivity myAddressActivity);
    void inject(NewOrderActivity newOrderActivity);
    void inject(PickAddressDialog pickAddressDialog);
    void inject(OrderActivity orderActivity);
    void inject(OrderSuccessActivity orderSuccessActivity);
    void inject(CommonListFragment commonListFragment);
    void inject(ItemListActivity itemListActivity);
    void inject(CouponActivity couponActivity);
    void inject(InboxActivity inboxActivity);
    void inject(AccountRecordActivity recordActivity);
    void inject(PaymentActivity paymentActivity);
    void inject(FeedbackActivity feedbackActivity);
    void inject(DeliverActivity deliverActivity);
    void inject(SettingActivity settingActivity);
}
