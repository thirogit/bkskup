package com.bk.bkskup3.management;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bk.bkskup3.BkListActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.settings.AdminPassword;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 1:30 PM
 */
public class ManagementActivity extends BkListActivity {

    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

    class ManagementItem {
        String title;
        Drawable icon;
        View.OnClickListener onClick;
    }

    class ManagementItemBuilder {
        int mTitleResource;
        int mIconResource;
        View.OnClickListener mAction;

        ManagementItemBuilder title(int titleResource) {
            this.mTitleResource = titleResource;
            return this;
        }

        ManagementItemBuilder icon(int iconResource) {
            this.mIconResource = iconResource;
            return this;
        }

        ManagementItemBuilder action(View.OnClickListener action) {
            this.mAction = action;
            return this;
        }

        ManagementItem build() {
            Resources r = getResources();
            ManagementItem item = new ManagementItem();
            item.icon = r.getDrawable(mIconResource);
            item.title = r.getString(mTitleResource);
            item.onClick = mAction;

            return item;
        }

    }


    private static final int CHANGE_PASSWORD_RQ_CODE = 101;

    @Inject
    SettingsStore mSettingsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ManagementItem items[] = {

                new ManagementItemBuilder()
                        .title(R.string.accessManagementItemTitle)
                        .icon(R.drawable.ic_action_security)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAccess();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.repoManagementItemTitle)
                        .icon(R.drawable.ic_action_mail_outline)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRepo();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.herdsManagementItemTitle)
                        .icon(R.drawable.ic_action_domain)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onHerds();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.stockManagementItemTitle)
                        .icon(R.drawable.ic_action_pets)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onStocks();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.classesManagementItemTitle)
                        .icon(R.drawable.ic_action_star_half)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClasses();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.hentsManagementItemTitle)
                        .icon(R.drawable.ic_action_people)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onHents();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.deductionManagementItemTitle)
                        .icon(R.drawable.ic_action_pie_chart)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onDeductions();
                            }
                        }).build(),


                new ManagementItemBuilder()
                        .title(R.string.agentManagementItemTitle)
                        .icon(R.drawable.ic_action_account_box)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAgent();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.companyManagementItemTitle)
                        .icon(R.drawable.ic_action_account_balance)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCompany();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.invSettingsManagementItemTitle)
                        .icon(R.drawable.ic_action_local_grocery_store)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onInvoiceSettings();
                            }


                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.vatRatesManagementItemTitle)
                        .icon(R.drawable.ic_action_monetization_on)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onVatRates();
                            }
                        }).build(),

                new ManagementItemBuilder()
                        .title(R.string.docsCfgManagementItemTitle)
                        .icon(R.drawable.ic_action_print)
                        .action(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onDocsCfg();
                            }
                        }).build(),




        };

        ArrayAdapter<ManagementItem> itemsAdapter = new ArrayAdapter<ManagementItem>(this, R.layout.management_list_item, R.id.item_title, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.management_list_item, null);
                } else {
                    row = convertView;
                }

                ImageView itemIcon = (ImageView) row.findViewById(R.id.item_icon);
                ManagementItem item = getItem(position);
                itemIcon.setImageDrawable(item.icon);

                TextView itemTitle = (TextView) row.findViewById(R.id.item_title);
                itemTitle.setText(item.title);

                return row;
            }
        };

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ManagementItem item = (ManagementItem) parent.getItemAtPosition(position);
                item.onClick.onClick(view);
            }
        });
        setListAdapter(itemsAdapter);
    }

    private void onInvoiceSettings() {
        startActivity(new Intent(this,InvoiceSettingsActivity.class));
    }

    private void onDocsCfg() {
        startActivity(new Intent(this,DocumentsCfgActivity.class));
    }

    private void onRepo() {
        startActivity(new Intent(this, RepoActivity.class));
    }

    private void onHents() {
        startActivity(new Intent(this, HentsActivity.class));
    }

    private void onDeductions() {
        startActivity(new Intent(this, DeductionsActivity.class));
    }

    private void onClasses() {
        startActivity(new Intent(this, ClassesActivity.class));
    }

    private void onStocks() {
        startActivity(new Intent(this, StocksActivity.class));
    }

    private void onVatRates() {
        startActivity(new Intent(this, TaxRatesActivity.class));
    }

    private void onAccess() {

        AdminPassword adminPassword = mSettingsStore.loadSettings(AdminPassword.class);

        if(Strings.isNullOrEmpty(adminPassword.getPassword()))
        {
            adminPassword.setPassword(DEFAULT_ADMIN_PASSWORD);
        }

        Intent adminPasswdChange = new Intent(this, ChangePassword.class);
        adminPasswdChange.putExtra(ChangePassword.EXTRA_OLD_PASSWD_HASH, adminPassword.getPassword());
        startActivityForResult(adminPasswdChange, CHANGE_PASSWORD_RQ_CODE);
    }

    private void onCompany() {
        startActivity(new Intent(this, CompanyActivity.class));
    }

    private void onAgent() {
        startActivity(new Intent(this, AgentActivity.class));
    }

    private void onHerds() {
        startActivity(new Intent(this, HerdsManagementActivity.class));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_PASSWORD_RQ_CODE && resultCode == RESULT_OK) {
            String newPasswd = data.getStringExtra(ChangePassword.EXTRA_NEW_PASSWD_HASH);
            AdminPassword adminPassword = new AdminPassword();
            adminPassword.setPassword(newPasswd);
            mSettingsStore.saveSettings(adminPassword);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//
//        inflater.inflate(R.menu.finish_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menuFinish:
//                setResult(RESULT_CANCELED);
//                finish();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
