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
        const { source, imgUrl, onPlay } = this.props;
        return (
            <View  style={{ height: 90, width: 100 }} >
                <TouchableOpacity
                    style={{ height: 90, width: 100, }}
                    onPress={(data) => onPlay(source) }>

                    <Image
                        style={{ height: 90, width: 100, }}
                        source={ require('../asserts/images/rmb.jpg') }
                        resizeMode="contain" />

                </TouchableOpacity>
            </View>
        );
    }
}

PlayItem.propTypes = {
    source: PropTypes.number.isRequired,
    imgUrl: PropTypes.string.isRequired,
    onPlay: PropTypes.func.isRequired,
};

export default PlayItem;