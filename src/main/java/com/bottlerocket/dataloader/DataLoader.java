package com.bottlerocket.dataloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ford.arnett on 12/6/16.
 */
public interface DataLoader {
    ArrayList<ArrayList<Object>> loadData() throws IOException;

    public default Object[][] twoDListToArray(List<ArrayList<Object>> list) {
        int columns = 0;
        for (List x : list) {
            if(x.size() > columns){
                columns = x.size();
            }
        }

        Object[][] results = new Object[list.size()][columns];
        for (int x = 0; x < list.size(); x++) {
            results[x] = list.get(x).toArray();
        }

        return results;
    }
}
