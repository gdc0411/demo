
import React, { Component } from 'react';
import { on, remove } from './event';
import { getValue } from './count';

export const connector = (name, __Component) => {

    class SimpleEventConnector extends Component {

        constructor(props) {
            super(props);
            this.state = {
                data: getValue(),
            };
            //创建事件处理handler
            this.handler = ((data) => {
                this.setState({
                    data: data
                });
            }).bind(this);
        }
        /**
         * 注册事件监听
         */
        componentDidMount() {
            on(name, this.handler);
        }
        /**
         * 删除事件监听
         */
        componentWillUnmount() {
            remove(name, this.handler);
        }

        render() {
            const {data} = this.state;
            return <__Component data={data} />;
        }

    }
    return SimpleEventConnector;
};