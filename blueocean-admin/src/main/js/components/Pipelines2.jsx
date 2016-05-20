import React, { Component, PropTypes } from 'react';
import PipelineRowItem from './PipelineRowItem';
import { PipelineRecord } from './records';
import Table from './Table';

import { Page, PageHeader, Title, Favorite, WeatherIcon } from '@jenkins-cd/design-language';
import { ExtensionPoint } from '@jenkins-cd/js-extensions';

const { array } = PropTypes;

export default class Pipelines2 extends Component {

    render() {
        const { pipelines } = this.context;

        console.log('Pipeline2 got pipelines=', pipelines);
        debugger;

        // Early out
        if (!pipelines) {
            return <div>No pipelines found.</div>;
        }

        const pipelineRecords = pipelines
            .map(data => new PipelineRecord(data))
            .sort(pipeline => !!pipeline.branchNames);

        return (
            <Page>
                <PageHeader>
                    <Title>
                        <h1>Dashboard</h1>
                        <a
                          target="_blank"
                          className="btn-inverse"
                          href="/jenkins/view/All/newJob"
                        >
                            New Pipeline
                        </a>
                    </Title>
                </PageHeader>
                <main>
                    <article>
                        <ExtensionPoint name="jenkins.pipeline.list.top" />
                        <Table
                          className="multiBranch"
                          headers={['Name', 'Health', 'Branches', 'Pull Requests', '']}
                        >
                            { pipelineRecords
                                .map(pipeline => (
                                    <tr key={pipeline.name}>
                                        <td>{pipeline.name}</td>
                                        <td><WeatherIcon score={pipeline.weatherScore} /></td>
                                        {
                                            // fixme refactor the next 2 lines and the prior logic
                                            // to create a react component out of it
                                        }
                                        <td>-</td>
                                        <td>-</td>
                                        <td><Favorite /></td>
                                    </tr>
                                ))
                            }
                        </Table>
                    </article>
                </main>
            </Page>);
    }
}

Pipelines2.contextTypes = {
    pipelines: array,
};
