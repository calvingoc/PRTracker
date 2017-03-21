package online.cagocapps.prtracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import online.cagocapps.prtracker.Data.ProfileContract;

/**
 * Created by cgehredo on 3/21/2017.
 */

public class ViewResultsRecycAdapter extends RecyclerView.Adapter<ViewResultsRecycAdapter.viewResultsRecycAdapterViewHolder> {
    private Number[] results;
    private Long[] dates;
    private int[] pr;
    private int[] resultID;
    private int[] reps;
    private int[] sets;
    private String resultType;

    public class viewResultsRecycAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvDate;
        public final ImageView imPR;
        public final TextView tvResult;

        public viewResultsRecycAdapterViewHolder(View view){
            super(view);
            tvDate = (TextView) view.findViewById(R.id.vr_tv_date);
            imPR = (ImageView) view.findViewById(R.id.vr_iv_pr);
            tvResult = (TextView) view.findViewById(R.id.vr_tv_results);
        }
    }

    @Override
    public viewResultsRecycAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_results_item, parent, false);
        return new viewResultsRecycAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewResultsRecycAdapterViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dates[position]);
        String formattedDate = Integer.toString(calendar.get(Calendar.MONTH)) + "/"
                + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                Integer.toString(calendar.get(Calendar.YEAR));
        holder.tvDate.setText(formattedDate);
        if(pr[position] == 1){
            holder.imPR.setVisibility(View.VISIBLE);
        } else holder.imPR.setVisibility(View.INVISIBLE);
        if(resultType.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
            String formattedResults = Integer.toString(sets[position]) + " X " + Integer.toString(reps[position]) + " X " + Integer.toString(results[position].intValue());
            holder.tvResult.setText(formattedResults);
        } else if (resultType.equals(ProfileContract.Running.TIME)){
            String hours = Integer.toString(results[position].intValue() / 3600) + ":";
            String minutes = Integer.toString((results[position].intValue() % 3600) / 60) + ":";
            String seconds = Integer.toString(((results[position].intValue() % 3600) % 60));
            holder.tvResult.setText(hours + minutes + seconds);
        } else holder.tvResult.setText(results[position].toString());

    }

    @Override
    public int getItemCount() {
        return results.length;
    }

    public void setVariables(Number[] resultsArray, Long[] datesArray, int[] prArray, int[] resultIDArray,
                             int[] repsArray, int[] setsArray, String type){
        results = resultsArray;
        dates = datesArray;
        pr = prArray;
        resultID = resultIDArray;
        reps = repsArray;
        sets = setsArray;
        resultType = type;
    }
}
