import React, {PropTypes} from 'react';

export default propTypes = {
    // 用playMode接受指定的值。
    //playModeEnum: React.PropTypes.oneOf([10000, 10002]),

    //URL数据源
    uriSrc: PropTypes.shape({
        playMode: PropTypes.number.isRequired,
        uri: PropTypes.string.isRequired,
        pano: PropTypes.bool.isRequired,
        hasSkin: PropTypes.bool.isRequired,
    }),
    //VOD数据源
    vodSrc: PropTypes.shape({
        playMode: PropTypes.number.isRequired,
        uuid: PropTypes.string.isRequired,
        vuid: PropTypes.string.isRequired,
        businessline: PropTypes.string.isRequired,
        saas: PropTypes.bool.isRequired,
        pano: PropTypes.bool.isRequired,
        hasSkin: PropTypes.bool.isRequired,
    }),
    //LIVE数据源    
    liveSrc: PropTypes.shape({
        playMode: PropTypes.number.isRequired,
        actionId: PropTypes.string.isRequired,
        usehls: PropTypes.bool.isRequired,
        customerId: PropTypes.string.isRequired,
        businessline: PropTypes.string.isRequired,
        cuid: PropTypes.string.isRequired,
        utoken: PropTypes.string.isRequired,
        pano: PropTypes.bool.isRequired,
        hasSkin: PropTypes.bool.isRequired,
    }),

    //数据源
    dataSource: PropTypes.shape({
        playMode: PropTypes.number.isRequired,
        uuid: PropTypes.string.isRequired,
        vuid: PropTypes.string.isRequired,
        businessline: PropTypes.string.isRequired,
        saas: PropTypes.bool.isRequired,
        pano: PropTypes.bool.isRequired,
        hasSkin: PropTypes.bool.isRequired,
    }).isRequired,
};