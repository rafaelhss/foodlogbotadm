(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('ScheduledMeal', ScheduledMeal);

    ScheduledMeal.$inject = ['$resource'];

    function ScheduledMeal ($resource) {
        var resourceUrl =  'api/scheduled-meals/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
