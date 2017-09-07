(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('BodyLog', BodyLog);

    BodyLog.$inject = ['$resource', 'DateUtils'];

    function BodyLog ($resource, DateUtils) {
        var resourceUrl =  'api/body-logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.bodyLogDatetime = DateUtils.convertDateTimeFromServer(data.bodyLogDatetime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
