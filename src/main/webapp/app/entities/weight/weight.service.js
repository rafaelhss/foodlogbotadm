(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('Weight', Weight);

    Weight.$inject = ['$resource', 'DateUtils'];

    function Weight ($resource, DateUtils) {
        var resourceUrl =  'api/weights/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.weightDateTime = DateUtils.convertDateTimeFromServer(data.weightDateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
