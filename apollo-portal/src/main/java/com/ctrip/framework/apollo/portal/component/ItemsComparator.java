package com.ctrip.framework.apollo.portal.component;

import com.ctrip.framework.apollo.common.dto.ItemChangeSets;
import com.ctrip.framework.apollo.common.dto.ItemDTO;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import com.ctrip.framework.apollo.core.utils.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ItemsComparator {


  public ItemChangeSets compareIgnoreBlankAndCommentItem(long baseNamespaceId, List<ItemDTO> baseItems, List<ItemDTO> targetItems){
    List<ItemDTO> filteredSourceItems = filterBlankAndCommentItem(baseItems);
    List<ItemDTO> filteredTargetItems = filterBlankAndCommentItem(targetItems);

    Map<String, ItemDTO> sourceItemMap = BeanUtils.mapByKey("key", filteredSourceItems);
    Map<String, ItemDTO> targetItemMap = BeanUtils.mapByKey("key", filteredTargetItems);

    ItemChangeSets changeSets = new ItemChangeSets();

    for (ItemDTO item: targetItems){//遍历灰度的item集合
      String key = item.getKey();

      ItemDTO sourceItem = sourceItemMap.get(key);
      if (sourceItem == null){//add master没有增标记未新增
        ItemDTO copiedItem = copyItem(item);
        copiedItem.setNamespaceId(baseNamespaceId);
        changeSets.addCreateItem(copiedItem);
      }else if (!Objects.equals(sourceItem.getValue(), item.getValue())){//update master有但是值不一致，则标记未更新
        //only value & comment can be update
        sourceItem.setValue(item.getValue());
        sourceItem.setComment(item.getComment());
        changeSets.addUpdateItem(sourceItem);
      }
    }

    for (ItemDTO item: baseItems){
      String key = item.getKey();

      ItemDTO targetItem = targetItemMap.get(key);
      if(targetItem == null){//delete //如果master有但是灰度分支没有，则标记未delete
        changeSets.addDeleteItem(item);
      }
    }

    return changeSets;
  }

  private List<ItemDTO> filterBlankAndCommentItem(List<ItemDTO> items){

    List<ItemDTO> result = new LinkedList<>();

    if (CollectionUtils.isEmpty(items)){
      return result;
    }

    for (ItemDTO item: items){
      if (!StringUtils.isEmpty(item.getKey())){
        result.add(item);
      }
    }

    return result;
  }

  private ItemDTO copyItem(ItemDTO sourceItem){
    ItemDTO copiedItem = new ItemDTO();
    copiedItem.setKey(sourceItem.getKey());
    copiedItem.setValue(sourceItem.getValue());
    copiedItem.setComment(sourceItem.getComment());
    return copiedItem;

  }

}
