(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-telegram', {
            parent: 'entity',
            url: '/user-telegram',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'UserTelegrams'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-telegram/user-telegrams.html',
                    controller: 'UserTelegramController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('user-telegram-detail', {
            parent: 'user-telegram',
            url: '/user-telegram/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'UserTelegram'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-telegram/user-telegram-detail.html',
                    controller: 'UserTelegramDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'UserTelegram', function($stateParams, UserTelegram) {
                    return UserTelegram.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-telegram',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-telegram-detail.edit', {
            parent: 'user-telegram-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-telegram/user-telegram-dialog.html',
                    controller: 'UserTelegramDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserTelegram', function(UserTelegram) {
                            return UserTelegram.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-telegram.new', {
            parent: 'user-telegram',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-telegram/user-telegram-dialog.html',
                    controller: 'UserTelegramDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                telegramId: null,
                                first_name: null,
                                last_name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-telegram', null, { reload: 'user-telegram' });
                }, function() {
                    $state.go('user-telegram');
                });
            }]
        })
        .state('user-telegram.edit', {
            parent: 'user-telegram',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-telegram/user-telegram-dialog.html',
                    controller: 'UserTelegramDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserTelegram', function(UserTelegram) {
                            return UserTelegram.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-telegram', null, { reload: 'user-telegram' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-telegram.delete', {
            parent: 'user-telegram',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-telegram/user-telegram-delete-dialog.html',
                    controller: 'UserTelegramDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserTelegram', function(UserTelegram) {
                            return UserTelegram.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-telegram', null, { reload: 'user-telegram' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
