import React, {Component, PropTypes} from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    TouchableOpacity,
} from 'react-native';

import bizProtoType  from './proto';

class PlayItem extends Component {

    render() {
        const { source, desc, color, imgUrl, onPlay } = this.props;
        return (
            <View  style={{ height: 60 }} >
                <TouchableOpacity  onPress={(data) => onPlay(source) }>
                    <Text style={{ fontSize: 18, fontWeight:'bold', color:`${color}` }}>{desc}</Text>
                    {/** <Image
                        style={{ height: 90, width: 100, }}
                        source={ require('../asserts/images/rmb.jpg') }
                        resizeMode="contain" /> */}

                </TouchableOpacity>
            </View>
        );
    }
}

PlayItem.propTypes = {
    source: PropTypes.number.isRequired,
    imgUrl: PropTypes.string.isRequired,
    desc: PropTypes.string.isRequired,
    color: PropTypes.string.isRequired,
    onPlay: PropTypes.func.isRequired,
};

export default PlayItem;