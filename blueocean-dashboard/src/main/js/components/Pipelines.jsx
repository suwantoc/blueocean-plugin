import React, { Component, PropTypes } from 'react';
import { Link } from 'react-router';
import PipelineRowItem from './PipelineRowItem';
import { PipelineRecord } from './records';

import { Page, PageHeader, Table, Title } from '@jenkins-cd/design-language';
import { ExtensionPoint } from '@jenkins-cd/js-extensions';

const { array } = PropTypes;

export default class Pipelines extends Component {

    render() {
        const { pipelines, config } = this.context;
        const { organization } = this.context.params;

        // Early out
        if (!pipelines) {
            return <div>No pipelines found.</div>;
        }

        const orgLink = organization ?
            <Link to={`organizations/${organization}`} className="inverse">
                {organization}
            </Link> : '';

        const pipelineRecords = pipelines
            .map(data => new PipelineRecord(data))
            .filter(data => !data.isFolder())
            .sort(pipeline => !!pipeline.branchNames);

        const headers = [
            { label: 'Name', className: 'name' },
            'Health',
            'Branches',
            'Pull Requests',
            { label: '', className: 'favorite' },
        ];

        const baseUrl = config.getRootURL();
        const newJobUrl = `${baseUrl}view/All/newJob`;

        return (
            <Page>
                <PageHeader>
                    <Title>
                        <h1>
                            <Link to="/" className="inverse">Dashboard</Link>
                            { organization && ' / ' }
                            { organization && orgLink }
                        </h1>
                        <a target="_blank" className="btn-secondary inverse" href={newJobUrl}>
                            New Pipeline
                        </a>
                    </Title>
                </PageHeader>
                <main>
                    <article>
                        <ExtensionPoint name="jenkins.pipeline.list.top" />
                        <Table
                          className="pipelines-table fixed"
                          headers={headers}
                        >
                            { pipelineRecords
                                .map(pipeline => <PipelineRowItem
                                  key={pipeline.name} pipeline={pipeline}
                                  showOrganization={!organization}
                                />)
                            }
                        </Table>
                    </article>
                </main>
            </Page>);
    }
}

Pipelines.contextTypes = {
    config: PropTypes.object,
    params: PropTypes.object,
    pipelines: array,
};
