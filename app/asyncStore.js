/**
 * AsyncStorage Demo
 * 
 */
import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    ScrollView,
    Image,
    AsyncStorage,
    TouchableOpacity,
    BackAndroid,
    View,
} from 'react-native';

import Dimensions from 'Dimensions';

const width = Dimensions.get('window').width;
const height = Dimensions.get('window').height;

const goodsData = [
    {
        id: '1',
        title: '猕猴桃1',
        desc: '12个装',
        price: 99,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
    {
        id: '2',
        title: '牛油果2',
        desc: '6个装',
        price: 59,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
    {
        id: '3',
        title: '猕猴桃3',
        desc: '3个装',
        price: 993,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
    {
        id: '4',
        title: '猕猴桃4',
        desc: '4个装',
        price: 994,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
    {
        id: '5',
        title: '猕猴桃5',
        desc: '5个装',
        price: 995,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
    {
        id: '6',
        title: '猕猴桃6',
        desc: '6个装',
        price: 996,
        url: 'http://image18-c.poco.cn/mypoco/myphoto/20160610/18/17351665220160610181307073.jpg',
    },
];

/**
 * 
 * 购物车组件类
 * 
 * @export 外部入口
 * @class shoppingCart
 * @extends {Component}
 */
export default class shoppingCart extends Component {
    /**
     * Creates an instance of shoppingCart.
     * 
     * @param {any} props
     */
    constructor(props) {
        super(props);
    }

    /**
     * 渲染
     * @returns
     */
    render() {
        return (
            <List navigator={this.props.navigator} />
        );
    }
}


/**
 * 列表项目组件 
 * @class Item 组件名
 * @extends {Component}
 */
class Item extends Component {
    /**
     * 默认属性 
     * @static
     */
    static defaultProps = {
        url: 'https://gss0.bdstatic.com/5eR1dDebRNRTm2_p8IuM_a/res/img/richanglogo168_24.png',
        title: '默认标题',
    };

    /**
     * 属性类型
     * 
     * @static
     */
    static propTypes = {
        url: React.PropTypes.string.isRequired,
        title: React.PropTypes.string.isRequired,
    };


    render() {
        return (
            <View style={styles.item} >
                <TouchableOpacity onPress={this.props.press} >
                    <Image resizeMode='contain' style={styles.img} source={{ uri: this.props.url }} >
                        <Text numberOfLines={1} style={styles.item_text}>{this.props.title}</Text>
                    </Image>
                </TouchableOpacity>
            </View>
        );
    }
}


/**
 * 列表组件
 * 
 * @class List
 * @extends {Component}
 */
class List extends Component {
    constructor(props) {
        super(props);
        this.state = {
            count: 0,
            loaded: false,
        };
    }

    /**
     * 点击开始结算
     */
    _puchase = () => {
        //alert('结算');
        let _self = this;
        const {navigator} = this.props;
        if (navigator) {
            navigator.push({
                name: 'PurchaseOrder', component: PurchaseOrder,
                params: {
                    //返回更新购物车
                    updateCart: function (b) {
                        _self.setState({
                            loaded: b
                        });
                    }
                }
            });
        }
    }

    /**
     * 添加到购物车
     * @param {any} data 商品数据
     */
    _addToCart = (data) => {
        let _self = this;

        AsyncStorage.setItem('SP-' + this._getGUID() + '-SP', JSON.stringify(data), function (err) {
            if (err) {
                //TODO: 存储错误
                alert(err);
            } else {
                //保存成功
            }
            _self.setState({
                count: _self.state.count + 1,
            });
        });
    };

    /**
     * 获得全局ID的方法
     * @returns
     */
    _getGUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        }).toUpperCase();
    }


    /**
     * 组件全部渲染完成
     */
    componentDidMount() {
        //获取本组件的指针，给匿名函数调用
        let _self = this;

        AsyncStorage.getAllKeys(function (err, keys) {
            if (err) {
                //TODO 添加错误处理
                console.error(err);
            } else {
                console.log('成功读取数据:' + keys.toString());
                //渲染
                _self.setState({
                    count: keys.length,
                    loaded: true,
                });
            }
        });
    }

    /**
     * 渲染之前回调， 
     * @param {any} nextProps
     * @param {any} nextState
     */
    componentWillUpdate(nextProps, nextState) {
        //如果需要重新加载，则重新加载，否则跳过
        if (!this.state.loaded) {
            //获取本组件的指针，给匿名函数调用
            let _self = this;
            AsyncStorage.getAllKeys(function (err, keys) {
                if (err) {
                    //TODO 添加错误处理
                    console.error(err);
                } else {
                    console.log('成功读取数据:' + keys.toString());
                    //渲染
                    _self.setState({
                        count: keys.length,
                        loaded: true,
                    });
                }
            });
        }
    }



    /**
     * 
     * 
     * @returns
     */
    render() {
        var list = [];
        console.log('Cart渲染');
        for (let i in goodsData) {
            if (i % 2 === 0) { //三个等号：判断类型；两个等号 ：不判断类型
                let row = (
                    <View style={styles.row} key={i} >
                        <Item title={goodsData[i].title}
                            url={goodsData[i].url}
                            press={ (data) => this._addToCart(goodsData[i]) }
                            />

                        <Item title={goodsData[parseInt(i) + 1].title}
                            url={goodsData[parseInt(i) + 1].url}
                            press={ (data) => this._addToCart(goodsData[parseInt(i) + 1]) }
                            />
                    </View>
                );
                list.push(row);
            }
        }

        let count = this.state.count;
        let str = null;
        if (count) {
            str = ',共' + count + '件商品';
        }

        return (
            <ScrollView style={{ marginTop: 20 }} >
                {list}
                <Text onPress={this._puchase} style={styles.btn} >
                    去结算{str}
                </Text>
            </ScrollView>
        );
    }
}



/**
 * 购物车结算组件
 * @class PurchaseOrder
 * @extends {Component}
 */
class PurchaseOrder extends Component {
    constructor(props) {
        super(props);
        this.state = {
            
            price: 0,
            data: [],
            operated: false,
        };
    }

    componentDidMount() {
        //获得本组件的指针，提供给匿名函数
        let _self = this;

        //异步拿到全部的Keys
        AsyncStorage.getAllKeys(function (err, keys) {
            if (err) {
                //TODO:处理错误
                console.error(err);
                return false;
            }

            //根据Keys读取全部数据，放入this.state.data
            AsyncStorage.multiGet(keys, function (err, result) {
                if (err) {
                    //TODO:处理错误
                    console.error(err);
                    return false;
                }
                //得到的结构是二位数组
                let arr = [];
                for (let i in result) {
                    //取数组第二位的数据
                    arr.push(JSON.parse(result[i][1]));
                }
                //渲染
                _self.setState({
                    data: arr,
                });
            });
        }
        );
    }

    /**
     * 清空购物车
     */
    _clearChart = () => {
        let _self = this;

        //异步清除全部数据，并更新state
        AsyncStorage.clear(function (err) {
            if (err) {
                //TODO:处理错误
                console.error(err);
                return;
            }
            _self.setState({
                data: [],
                price: 0,
                operated: true,
            });
            alert('购物车已清空');
        });
    }

    /**
     * 返回购物界面
     */
    handleBack = () => {
        const {navigator} = this.props;
        const {updateCart} = this.props;
        if (this.state.operated && updateCart) {
            //更新列表状态，渲染
            console.log('回调更新状态');
            updateCart(false);
        }
        if (navigator) {
            navigator.pop();
            return true;
        }
    }

    render() {
        let price = this.state.price;
        let data = this.state.data;
        let list = [];
        for (let i in data) {
            price += parseFloat(data[i].price);
            list.push(
                <View style={[styles.row, styles.list_item]} key={i} >
                    <Text style={styles.list_item_desc}>
                        {data[i].title}{data[i].desc}
                    </Text>
                    <Text style={styles.list_item_price} >人民币: {data[i].price} </Text>
                </View>
            );
        }

        let str = null;
        if (price) {
            str = '，共' + price.toFixed(2) + '元';
        }
        return (
            <ScrollView style={{ marginTop: 20 }} >
                {list}
                <Text style={styles.btn} >支付{str} </Text>
                <Text style={styles.clear} onPress={this._clearChart} >清空购物车</Text>
                <Text style={styles.clear} onPress={this.handleBack} >返回</Text>
            </ScrollView>
        );
    }
}


const styles = StyleSheet.create({
    list_item: {
        marginLeft: 5,
        marginRight: 5,
        padding: 5,
        borderWidth: 1,
        height: 30,
        borderRadius: 3,
        borderColor: '#ddd',
    },
    list_item_desc: {
        flex: 2,
        fontSize: 15,
    },
    list_item_price: {
        flex: 1,
        fontSize: 15,
        textAlign: 'right',
    },
    clear: {
        marginTop: 10,
        backgroundColor: '#FF7200',
        color: '#fff',
        borderWidth: 1,
        borderColor: '#ddd',
        marginLeft: 10,
        marginRight: 10,
        lineHeight: 24,
        height: 33,
        fontSize: 20,
        textAlign: 'center',
        textAlignVertical: 'center',
    },
    btn: {
        flex: 1,
        backgroundColor: '#FF7200',
        height: 33,
        textAlign: 'center',
        textAlignVertical: 'center',
        color: '#fff',
        marginLeft: 10,
        marginRight: 10,
        lineHeight: 24,
        marginTop: 40,
        fontSize: 18,
    },
    row: {
        flexDirection: 'row',
        marginBottom: 10,
    },
    img: {
        flex: 1,
        backgroundColor: 'transparent',
    },
    item_text: {
        backgroundColor: '#000',
        opacity: 0.7,
        color: '#fff',
        height: 25,
        lineHeight: 18,
        textAlign: 'center',
        marginTop: 74,
    },
    item: {
        flex: 1,
        marginLeft: 5,
        borderWidth: 1,
        borderColor: '#ddd',
        marginRight: 5,
        height: 100,
    },
    list: {
        justifyContent: 'flex-start',
        flexDirection: 'row',
        flexWrap: 'wrap'
    },
    container: {

        flex: 1,
    },
    listView: {
        paddingTop: 20,
        backgroundColor: '#F5FCFF',
    },
    thumbnail: {
        width: 80,
        height: 80,
        borderRadius: 16,
    },
    //让rightContainer在父容器中占据Image之外剩下的全部空间。

    container1: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    title: {
        fontSize: 14,
        marginBottom: 8,
    },
    year: {
        fontSize: 14,
    },
});