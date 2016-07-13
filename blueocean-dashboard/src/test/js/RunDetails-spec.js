/**
 * Created by cmeyers on 7/13/16.
 */
import { prepareMount } from './util/EnzymeUtils';
prepareMount();

import React from 'react';
import { assert, expect } from 'chai';
import { mount, shallow } from 'enzyme';
import sinon from 'sinon';

import { RunDetails } from '../../main/js/components/RunDetails';
import { runs } from './data/runs/runs';

describe('RunDetails', () => {
    const config = {
        getRootURL: () => '/',
    };

    describe('test', () => {
        it('should work', () => {
            const navigateToLocation = sinon.spy();

            const props = {
                fetchRunsIfNeeded: () => undefined,
                runs,
                isMultiBranch: true,
                navigateToLocation,
            };

            const context = {
                config,
                params: {
                    branch: 'master',
                    runId: '2',
                },
                router: {
                    isActive: () => false,
                    createHref: () => null,
                },
                location: {},
            };

            const wrapper = mount(
                <RunDetails
                  fetchRunsIfNeeded={props.fetchRunsIfNeeded}
                  runs={props.runs}
                  isMultiBranch={props.isMultiBranch}>
                    <div>hi</div>
                </RunDetails>,
                { context },
            );

            assert.equal(wrapper.find('.closeButton').length, 1);
            wrapper.find('.closeButton').simulate('click');
            expect(navigateToLocation.calledOnce).to.equal(true);
        });
    });
});
