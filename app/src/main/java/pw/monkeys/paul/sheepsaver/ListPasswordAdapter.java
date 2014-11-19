package pw.monkeys.paul.sheepsaver;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Paul on 11/19/2014.
 */
public class ListPasswordAdapter extends ArrayAdapter<PasswordItem> {

    Context context;

    public ListPasswordAdapter(Context context, int resourceId,
                           List<PasswordItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView domainText;
        TextView usernameText;
        TextView passwordSaftyText;
        ProgressBar passwordSecurityBar;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PasswordItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.itemlayout, null);
            holder = new ViewHolder();
            holder.domainText = (TextView) convertView.findViewById(R.id.domainText);
            holder.usernameText = (TextView) convertView.findViewById(R.id.usernameText);
            holder.passwordSaftyText = (TextView) convertView.findViewById(R.id.passwordSaftyText);
            holder.passwordSecurityBar = (ProgressBar) convertView.findViewById(R.id.passwordSecurityBar);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.domainText.setText(rowItem.getDomain());
        holder.passwordSaftyText.setText("Password Strength: "+rowItem.getPasswordStrength()+ "%");
        holder.usernameText.setText("Username: "+rowItem.getUsername());
        holder.passwordSecurityBar.setMax(100);
        holder.passwordSecurityBar.setProgress(rowItem.getPasswordStrength());

        //causing NPE
        //if(rowItem.getUsername().equals("null")) {
        //    holder.usernameText.setVisibility(View.INVISIBLE);
        //}

        return convertView;
    }
}