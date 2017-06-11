(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('MealLog', MealLog);

    MealLog.$inject = ['$resource', 'DateUtils'];

    function MealLog ($resource, DateUtils) {
        var resourceUrl =  'api/meal-logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.mealDateTime = DateUtils.convertDateTimeFromServer(data.mealDateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
