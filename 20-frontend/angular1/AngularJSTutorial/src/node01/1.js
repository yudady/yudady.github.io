const _ = require('./lib/lodash');
const response = require('./localdata/data');

// sort
let listData = _.orderBy(response.value, ['displayOrder'], ['desc']);


let getRoot = function (items, menuId) {
    return _.filter(items, function (o) {
        return o.parentId === menuId;
    });
}

let getChildValue = function (entity, allRows) {
    let childArray = [];
    let subAllRow = [];
    for (let i = 0; i < allRows.length; i++) {
        let ch = allRows[i];
        if (ch.parentId === entity.menuId) {  // important
            childArray.push(ch);
        } else {
            subAllRow.push(ch);
        }
    }

    let childList = [];
    for (let i = 0; i < childArray.length; i++) {
        let childEntity = childArray[i];
        childList.push(getChildValue(childEntity, subAllRow));
    }
    return {
        'entity': entity,
        'childList': childList
    };
};

let menu_tree = [];
_.forEach(getRoot(listData, 0), function (entity) {
    menu_tree.push(getChildValue(entity, listData));
});


console.log(JSON.stringify(menu_tree, null, 2));
console.log("123");


